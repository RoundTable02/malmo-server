package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.aop.CheckCoupleMember;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.AnswerQuestionUseCase;
import makeus.cmc.malmo.application.port.in.GetQuestionAnswerUseCase;
import makeus.cmc.malmo.application.port.in.GetQuestionUseCase;
import makeus.cmc.malmo.application.port.out.ValidateMemberPort;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.service.CoupleDomainService;
import makeus.cmc.malmo.domain.service.CoupleQuestionDomainService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CoupleQuestionService implements GetQuestionUseCase, GetQuestionAnswerUseCase, AnswerQuestionUseCase {

    private final ValidateMemberPort validateMemberPort;
    private final MemberDomainService memberDomainService;
    private final CoupleQuestionDomainService coupleQuestionDomainService;
    private final CoupleDomainService coupleDomainService;

    @Override
    @CheckValidMember
    public GetQuestionResponse getTodayQuestion(GetTodayQuestionCommand command) {
        boolean isCouple = validateMemberPort.isCoupleMember(MemberId.of(command.getUserId()));

        if (isCouple) {
            // 커플 사용자에게는 오늘의 커플 질문을 제공
            // 멤버가 속한 Couple의 가장 레벨이 높은 CoupleQuestion을 조회
            CoupleId coupleId = coupleDomainService.getCoupleIdByMemberId(MemberId.of(command.getUserId()));
            CoupleQuestionDomainService.CoupleQuestionDto maxLevelQuestion =
                    coupleQuestionDomainService.getMaxLevelQuestionDto(MemberId.of(command.getUserId()), coupleId);

            // CoupleQuestion의 bothAnsweredAt이 now()의 전날인 경우 (날짜만 비교), 다음 단계의 CoupleQuestion을 생성
            if (coupleQuestionDomainService.needsNextQuestion(maxLevelQuestion.getBothAnsweredAt())) {
                // TODO : 이전 단계의 답변을 바탕으로 MemberMemory 생성 로직 필요

                CoupleQuestion newQuestion = coupleQuestionDomainService.createNextCoupleQuestion(coupleId, maxLevelQuestion.getLevel());

                return GetQuestionResponse.builder()
                        .coupleQuestionId(newQuestion.getId())
                        .title(newQuestion.getQuestion().getTitle())
                        .content(newQuestion.getQuestion().getContent())
                        .meAnswered(false)
                        .partnerAnswered(false)
                        .level(newQuestion.getQuestion().getLevel())
                        .createdAt(newQuestion.getCreatedAt())
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
            TempCoupleQuestion tempCoupleQuestion = coupleQuestionDomainService.getTempCoupleQuestion(MemberId.of(command.getUserId()));

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
        CoupleId coupleId = coupleDomainService.getCoupleIdByMemberId(MemberId.of(command.getUserId()));
        CoupleQuestionDomainService.CoupleQuestionDto question =
                coupleQuestionDomainService.getCoupleQuestionDtoByLevel(MemberId.of(command.getUserId()), coupleId, command.getLevel());

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
            CoupleId coupleId = coupleDomainService.getCoupleIdByMemberId(MemberId.of(command.getUserId()));
            coupleQuestionDomainService.validateQuestionOwnership(
                    CoupleQuestionId.of(command.getCoupleQuestionId()),
                    coupleId
            );
            CoupleQuestionDomainService.MemberAnswersDto answers =
                    coupleQuestionDomainService.getQuestionAnswers(MemberId.of(command.getUserId()), CoupleQuestionId.of(command.getCoupleQuestionId()));


            return AnswerResponseDto.builder()
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
            Member member = memberDomainService.getMemberById(MemberId.of(command.getUserId()));
            TempCoupleQuestion tempCoupleQuestion = coupleQuestionDomainService.getTempCoupleQuestion(MemberId.of(command.getUserId()));

            return AnswerResponseDto.builder()
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
    public void answerQuestion(AnswerQuestionCommand command) {
        boolean isCouple = validateMemberPort.isCoupleMember(MemberId.of(command.getUserId()));

        if (isCouple) {
            // 커플 사용자에게는 커플 질문에 답변
            CoupleId coupleId = coupleDomainService.getCoupleIdByMemberId(MemberId.of(command.getUserId()));
            CoupleQuestion coupleQuestion = coupleQuestionDomainService.getMaxLevelQuestion(coupleId);

            // 답변을 저장
            coupleQuestionDomainService.answerQuestion(coupleQuestion, MemberId.of(command.getUserId()), command.getAnswer());

            // 커플 질문 개수 체크
            long count = coupleQuestionDomainService.countAnswers(CoupleQuestionId.of(coupleQuestion.getId()));

            // 커플 질문 저장
            if (count == 2) {
                // 두 명이 모두 답변한 경우, 상태를 업데이트
                coupleQuestionDomainService.updateQuestionComplete(coupleQuestion);
            }
        }
        else {
            // 커플이 아닌 사용자는 TempCoupleQuestion에 답변
            TempCoupleQuestion tempCoupleQuestion = coupleQuestionDomainService.getTempCoupleQuestion(MemberId.of(command.getUserId()));

            // 답변을 저장
            coupleQuestionDomainService.answerQuestion(tempCoupleQuestion, command.getAnswer());
        }
    }

    @Override
    @CheckValidMember
    public void updateAnswer(AnswerQuestionCommand request) {
        boolean isCouple = validateMemberPort.isCoupleMember(MemberId.of(request.getUserId()));

        if (isCouple) {
            // 커플 사용자에게는 커플 질문 답변 수정
            CoupleId coupleId = coupleDomainService.getCoupleIdByMemberId(MemberId.of(request.getUserId()));
            CoupleQuestion coupleQuestion = coupleQuestionDomainService.getMaxLevelQuestion(coupleId);

            // 답변을 수정
            coupleQuestionDomainService.updateAnswer(coupleQuestion, MemberId.of(request.getUserId()), request.getAnswer());
        }
        else {
            // 커플이 아닌 사용자는 TempCoupleQuestion 답변 수정
            TempCoupleQuestion tempCoupleQuestion = coupleQuestionDomainService.getTempCoupleQuestion(MemberId.of(request.getUserId()));

            // 답변을 수정
            coupleQuestionDomainService.updateAnswer(tempCoupleQuestion, request.getAnswer());
        }
    }
}
