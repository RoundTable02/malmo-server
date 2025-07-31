package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.aop.CheckCoupleMember;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.AnswerQuestionUseCase;
import makeus.cmc.malmo.application.port.in.GetQuestionAnswerUseCase;
import makeus.cmc.malmo.application.port.in.GetQuestionUseCase;
import makeus.cmc.malmo.application.port.out.ValidateMemberPort;
import makeus.cmc.malmo.application.service.helper.couple.CoupleQueryHelper;
import makeus.cmc.malmo.application.service.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.service.helper.question.CoupleQuestionCommandHelper;
import makeus.cmc.malmo.application.service.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.domain.exception.MemberAccessDeniedException;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.model.question.Question;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.service.CoupleQuestionDomainService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoupleQuestionService implements GetQuestionUseCase, GetQuestionAnswerUseCase, AnswerQuestionUseCase {

    private final ValidateMemberPort validateMemberPort;
    private final CoupleQuestionDomainService coupleQuestionDomainService;
    private final CoupleQueryHelper coupleQueryHelper;

    private final CoupleQuestionQueryHelper coupleQuestionQueryHelper;
    private final CoupleQuestionCommandHelper coupleQuestionCommandHelper;
    private final MemberQueryHelper memberQueryHelper;

    @Override
    @CheckValidMember
    @Transactional
    public GetQuestionResponse getTodayQuestion(GetTodayQuestionCommand command) {
        boolean isCouple = validateMemberPort.isCoupleMember(MemberId.of(command.getUserId()));

        if (isCouple) {
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

                return GetQuestionResponse.builder()
                        .coupleQuestionId(nextCoupleQuestion.getId())
                        .title(nextCoupleQuestion.getQuestion().getTitle())
                        .content(nextCoupleQuestion.getQuestion().getContent())
                        .meAnswered(false)
                        .partnerAnswered(false)
                        .level(nextCoupleQuestion.getQuestion().getLevel())
                        .createdAt(nextCoupleQuestion.getCreatedAt())
                        .build();
            }

            return GetQuestionResponse.builder()
                    .coupleQuestionId(maxLevelQuestion.getId())
                    .title(maxLevelQuestion.getTitle())
                    .content(maxLevelQuestion.getContent())
                    .meAnswered(maxLevelQuestion.isMeAnswered())
                    .partnerAnswered(maxLevelQuestion.isPartnerAnswered())
                    .level(maxLevelQuestion.getLevel())
                    .createdAt(maxLevelQuestion.getCreatedAt())
                    .build();
        }
        else {
            // 커플이 아닌 사용자는 1단계의 질문만 제공
            // TempCoupleQuestion을 조회, 없으면 생성
            TempCoupleQuestion tempCoupleQuestion = coupleQuestionQueryHelper.getTempCoupleQuestion(MemberId.of(command.getUserId()))
                    .orElseGet(() -> {
                        // TempCoupleQuestion이 없으면 생성
                        Question firstQuestion = coupleQuestionQueryHelper.getQuestionByLevelOrThrow(CoupleQuestionDomainService.FIRST_QUESTION_LEVEL);
                        TempCoupleQuestion tempQuestion = TempCoupleQuestion.create(MemberId.of(command.getUserId()), firstQuestion);
                        return coupleQuestionCommandHelper.saveTempCoupleQuestion(tempQuestion);
                    });

            return GetQuestionResponse.builder()
                    .coupleQuestionId(tempCoupleQuestion.getId())
                    .title(tempCoupleQuestion.getQuestion().getTitle())
                    .content(tempCoupleQuestion.getQuestion().getContent())
                    .meAnswered(tempCoupleQuestion.isAnswered())
                    .level(CoupleQuestionDomainService.FIRST_QUESTION_LEVEL)
                    .partnerAnswered(false)
                    .createdAt(tempCoupleQuestion.getCreatedAt())
                    .build();
        }
    }

    @Override
    @CheckCoupleMember
    public GetQuestionResponse getQuestion(GetQuestionCommand command) {
        CoupleId coupleId = coupleQueryHelper.getCoupleIdByMemberId(MemberId.of(command.getUserId()));
        CoupleQuestionQueryHelper.CoupleQuestionDto question =
                coupleQuestionQueryHelper.getCoupleQuestionDtoByLevel(MemberId.of(command.getUserId()), coupleId, command.getLevel());

        return GetQuestionResponse.builder()
                .coupleQuestionId(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .level(question.getLevel())
                .meAnswered(question.isMeAnswered())
                .partnerAnswered(question.isPartnerAnswered())
                .createdAt(question.getCreatedAt())
                .build();
    }

    @Override
    @CheckValidMember
    public AnswerResponseDto getQuestionAnswers(GetQuestionAnswerCommand command) {
        boolean isCouple = validateMemberPort.isCoupleMember(MemberId.of(command.getUserId()));

        if (isCouple) {
            // 커플 사용자에게는 커플 질문 답변을 조회
            // 커플 질문에 접근 권한이 있는지 확인
            CoupleId coupleId = coupleQueryHelper.getCoupleIdByMemberId(MemberId.of(command.getUserId()));
            coupleQuestionQueryHelper.validateQuestionOwnership(
                    CoupleQuestionId.of(command.getCoupleQuestionId()),
                    coupleId
            );
            CoupleQuestionQueryHelper.MemberAnswersDto answers =
                    coupleQuestionQueryHelper.getQuestionAnswers(MemberId.of(command.getUserId()), CoupleQuestionId.of(command.getCoupleQuestionId()));

            return AnswerResponseDto.builder()
                    .title(answers.getTitle())
                    .content(answers.getContent())
                    .level(answers.getLevel())
                    .createdAt(answers.getCreatedAt())
                    .me(
                            answers.getMe() == null ? null :
                            AnswerDto.builder()
                                    .nickname(answers.getMe().getNickname())
                                    .answer(answers.getMe().getAnswer())
                                    .updatable(answers.getMe().isUpdatable())
                                    .build()
                    )
                    .partner(
                            answers.getPartner() == null ? null :
                            AnswerDto.builder()
                                    .nickname(answers.getPartner().getNickname())
                                    .answer(answers.getPartner().getAnswer())
                                    .updatable(answers.getPartner().isUpdatable())
                                    .build()
                    )
                    .build();
        }
        else {
            // 커플이 아닌 사용자는 TempCoupleQuestion의 답변을 조회
            Member member = memberQueryHelper.getMemberByIdOrThrow(MemberId.of(command.getUserId()));
            TempCoupleQuestion tempCoupleQuestion = coupleQuestionQueryHelper.getTempCoupleQuestionOrThrow(MemberId.of(command.getUserId()));

            return AnswerResponseDto.builder()
                    .title(tempCoupleQuestion.getQuestion().getTitle())
                    .content(tempCoupleQuestion.getQuestion().getContent())
                    .level(CoupleQuestionDomainService.FIRST_QUESTION_LEVEL)
                    .createdAt(tempCoupleQuestion.getCreatedAt())
                    .me(
                            AnswerDto.builder()
                                    .nickname(member.getNickname())
                                    .answer(tempCoupleQuestion.getAnswer())
                                    .updatable(true)
                                    .build()
                    )
                    .partner(null) // 커플이 아닌 경우 파트너 답변은 없음
                    .build();
        }
    }

    @Override
    @CheckValidMember
    @Transactional
    public QuestionAnswerResponse answerQuestion(AnswerQuestionCommand command) {
        boolean isCouple = validateMemberPort.isCoupleMember(MemberId.of(command.getUserId()));

        if (isCouple) {
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

            return QuestionAnswerResponse.builder()
                    .coupleQuestionId(coupleQuestion.getId())
                    .build();
        }
        else {
            // 커플이 아닌 사용자는 TempCoupleQuestion에 답변
            TempCoupleQuestion tempCoupleQuestion = coupleQuestionQueryHelper.getTempCoupleQuestionOrThrow(MemberId.of(command.getUserId()));

            // 답변을 저장
            if (tempCoupleQuestion.isAnswered()) {
                throw new MemberAccessDeniedException("이미 답변한 질문입니다.");
            }
            tempCoupleQuestion.answerQuestion(command.getAnswer());

            coupleQuestionCommandHelper.saveTempCoupleQuestion(tempCoupleQuestion);

            return QuestionAnswerResponse.builder()
                    .coupleQuestionId(tempCoupleQuestion.getId())
                    .build();
        }
    }

    @Override
    @CheckValidMember
    @Transactional
    public QuestionAnswerResponse updateAnswer(AnswerQuestionCommand command) {
        boolean isCouple = validateMemberPort.isCoupleMember(MemberId.of(command.getUserId()));

        if (isCouple) {
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

            return QuestionAnswerResponse.builder()
                    .coupleQuestionId(coupleQuestion.getId())
                    .build();
        }
        else {
            // 커플이 아닌 사용자는 TempCoupleQuestion 답변 수정
            TempCoupleQuestion tempCoupleQuestion = coupleQuestionQueryHelper.getTempCoupleQuestionOrThrow(MemberId.of(command.getUserId()));

            // 답변을 수정
            if (!tempCoupleQuestion.isAnswered()) {
                throw new MemberAccessDeniedException("답변이 완료되지 않은 질문입니다.");
            }
            tempCoupleQuestion.updateAnswer(command.getAnswer());

            coupleQuestionCommandHelper.saveTempCoupleQuestion(tempCoupleQuestion);

            return QuestionAnswerResponse.builder()
                    .coupleQuestionId(tempCoupleQuestion.getId())
                    .build();
        }
    }
}
