package makeus.cmc.malmo.application.service.strategy;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.AnswerQuestionUseCase;
import makeus.cmc.malmo.application.port.in.GetQuestionAnswerUseCase;
import makeus.cmc.malmo.application.port.in.GetQuestionUseCase;
import makeus.cmc.malmo.application.service.helper.couple.CoupleQueryHelper;
import makeus.cmc.malmo.application.service.helper.question.CoupleQuestionCommandHelper;
import makeus.cmc.malmo.application.service.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.domain.exception.MemberAccessDeniedException;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.model.question.Question;
import makeus.cmc.malmo.domain.service.CoupleQuestionDomainService;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoupleQuestionStrategy implements QuestionHandlingStrategy{

    private final CoupleQuestionQueryHelper coupleQuestionQueryHelper;
    private final CoupleQuestionCommandHelper coupleQuestionCommandHelper;
    private final CoupleQuestionDomainService coupleQuestionDomainService;

    private final CoupleQueryHelper coupleQueryHelper;

    @Override
    public GetQuestionUseCase.GetQuestionResponse getTodayQuestion(GetQuestionUseCase.GetTodayQuestionCommand command) {
        // 커플 사용자에게는 오늘의 커플 질문을 제공
        // 멤버가 속한 Couple의 가장 레벨이 높은 CoupleQuestion을 조회
        CoupleId coupleId = coupleQueryHelper.getCoupleIdByMemberId(MemberId.of(command.getUserId()));
        CoupleQuestionQueryHelper.CoupleQuestionDto maxLevelQuestion =
                coupleQuestionQueryHelper.getMaxLevelQuestionDto(MemberId.of(command.getUserId()), coupleId);

        // CoupleQuestion의 bothAnsweredAt이 now()의 전날인 경우 (날짜만 비교), 다음 단계의 CoupleQuestion을 생성
        if (coupleQuestionDomainService.needsNextQuestion(maxLevelQuestion.getBothAnsweredAt())) {
            CoupleQuestion coupleQuestion = coupleQuestionQueryHelper.getMaxLevelQuestionOrThrow(coupleId);
            coupleQuestion.expire();
            coupleQuestionCommandHelper.saveCoupleQuestion(coupleQuestion);

            Question nextQuestion = coupleQuestionQueryHelper.getQuestionByLevelOrThrow(maxLevelQuestion.getLevel());
            CoupleQuestion nextCoupleQuestion = CoupleQuestion.createCoupleQuestion(nextQuestion, coupleId);

            return GetQuestionUseCase.GetQuestionResponse.builder()
                    .coupleQuestionId(nextCoupleQuestion.getId())
                    .title(nextCoupleQuestion.getQuestion().getTitle())
                    .content(nextCoupleQuestion.getQuestion().getContent())
                    .meAnswered(false)
                    .partnerAnswered(false)
                    .level(nextCoupleQuestion.getQuestion().getLevel())
                    .createdAt(nextCoupleQuestion.getCreatedAt())
                    .build();
        }

        return GetQuestionUseCase.GetQuestionResponse.builder()
                .coupleQuestionId(maxLevelQuestion.getId())
                .title(maxLevelQuestion.getTitle())
                .content(maxLevelQuestion.getContent())
                .meAnswered(maxLevelQuestion.isMeAnswered())
                .partnerAnswered(maxLevelQuestion.isPartnerAnswered())
                .level(maxLevelQuestion.getLevel())
                .createdAt(maxLevelQuestion.getCreatedAt())
                .build();
    }

    @Override
    public GetQuestionAnswerUseCase.AnswerResponseDto getQuestionAnswers(GetQuestionAnswerUseCase.GetQuestionAnswerCommand command) {
        // 커플 사용자에게는 커플 질문 답변을 조회
        // 커플 질문에 접근 권한이 있는지 확인
        CoupleId coupleId = coupleQueryHelper.getCoupleIdByMemberId(MemberId.of(command.getUserId()));
        coupleQuestionQueryHelper.validateQuestionOwnership(
                CoupleQuestionId.of(command.getCoupleQuestionId()),
                coupleId
        );
        CoupleQuestionQueryHelper.MemberAnswersDto answers =
                coupleQuestionQueryHelper.getQuestionAnswers(MemberId.of(command.getUserId()), CoupleQuestionId.of(command.getCoupleQuestionId()));

        return GetQuestionAnswerUseCase.AnswerResponseDto.builder()
                .title(answers.getTitle())
                .content(answers.getContent())
                .level(answers.getLevel())
                .createdAt(answers.getCreatedAt())
                .me(
                        answers.getMe() == null ? null :
                                GetQuestionAnswerUseCase.AnswerDto.builder()
                                        .nickname(answers.getMe().getNickname())
                                        .answer(answers.getMe().getAnswer())
                                        .updatable(answers.getMe().isUpdatable())
                                        .build()
                )
                .partner(
                        answers.getPartner() == null ? null :
                                GetQuestionAnswerUseCase.AnswerDto.builder()
                                        .nickname(answers.getPartner().getNickname())
                                        .answer(answers.getPartner().getAnswer())
                                        .updatable(answers.getPartner().isUpdatable())
                                        .build()
                )
                .build();
    }

    @Override
    public AnswerQuestionUseCase.QuestionAnswerResponse answerQuestion(AnswerQuestionUseCase.AnswerQuestionCommand command) {
        // 커플 사용자에게는 커플 질문에 답변
        CoupleId coupleId = coupleQueryHelper.getCoupleIdByMemberId(MemberId.of(command.getUserId()));
        CoupleQuestion coupleQuestion = coupleQuestionQueryHelper.getMaxLevelQuestionOrThrow(coupleId);

        // 답변을 저장
        CoupleMemberId coupleMemberId = coupleQueryHelper.getCoupleMemberIdByMemberId(MemberId.of(command.getUserId()));
        coupleQuestionQueryHelper.validateMemberAlreadyAnswered(CoupleQuestionId.of(coupleQuestion.getId()), MemberId.of(command.getUserId()));
        MemberAnswer memberAnswer = coupleQuestion.createMemberAnswer(coupleMemberId, command.getAnswer());
        coupleQuestionCommandHelper.saveMemberAnswer(memberAnswer);

        // 커플 질문 개수 체크
        long count = coupleQuestionQueryHelper.countAnswers(CoupleQuestionId.of(coupleQuestion.getId()));

        // 커플 질문 저장
        if (count == 2) {
            // 두 명이 모두 답변한 경우, 상태를 업데이트
            coupleQuestion.complete();
            coupleQuestionCommandHelper.saveCoupleQuestion(coupleQuestion);
        }

        return AnswerQuestionUseCase.QuestionAnswerResponse.builder()
                .coupleQuestionId(coupleQuestion.getId())
                .build();
    }

    @Override
    public AnswerQuestionUseCase.QuestionAnswerResponse updateAnswer(AnswerQuestionUseCase.AnswerQuestionCommand command) {
        // 커플 사용자에게는 커플 질문 답변 수정
        CoupleId coupleId = coupleQueryHelper.getCoupleIdByMemberId(MemberId.of(command.getUserId()));
        CoupleQuestion coupleQuestion = coupleQuestionQueryHelper.getMaxLevelQuestionOrThrow(coupleId);

        // 답변을 수정
        if (!coupleQuestion.isUpdatable()) {
            throw new MemberAccessDeniedException("이미 답변이 완료된 질문입니다.");
        }
        MemberAnswer memberAnswer = coupleQuestionQueryHelper.getMemberAnswerOrThrow(
                CoupleQuestionId.of(coupleQuestion.getId()),
                MemberId.of(command.getUserId())
        );

        memberAnswer.updateAnswer(command.getAnswer());

        return AnswerQuestionUseCase.QuestionAnswerResponse.builder()
                .coupleQuestionId(coupleQuestion.getId())
                .build();
    }
}
