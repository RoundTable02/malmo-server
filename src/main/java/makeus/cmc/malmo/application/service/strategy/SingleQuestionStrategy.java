package makeus.cmc.malmo.application.service.strategy;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.AnswerQuestionUseCase;
import makeus.cmc.malmo.application.port.in.GetQuestionAnswerUseCase;
import makeus.cmc.malmo.application.port.in.GetQuestionUseCase;
import makeus.cmc.malmo.application.service.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.service.helper.question.CoupleQuestionCommandHelper;
import makeus.cmc.malmo.application.service.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.domain.exception.MemberAccessDeniedException;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.question.Question;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.service.CoupleQuestionDomainService;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SingleQuestionStrategy implements QuestionHandlingStrategy{

    private final MemberQueryHelper memberQueryHelper;

    private final CoupleQuestionQueryHelper coupleQuestionQueryHelper;
    private final CoupleQuestionCommandHelper coupleQuestionCommandHelper;

    @Override
    public GetQuestionUseCase.GetQuestionResponse getTodayQuestion(GetQuestionUseCase.GetTodayQuestionCommand command) {
        // 커플이 아닌 사용자는 1단계의 질문만 제공
        // TempCoupleQuestion을 조회, 없으면 생성
        TempCoupleQuestion tempCoupleQuestion = coupleQuestionQueryHelper.getTempCoupleQuestion(MemberId.of(command.getUserId()))
                .orElseGet(() -> {
                    // TempCoupleQuestion이 없으면 생성
                    Question firstQuestion = coupleQuestionQueryHelper.getQuestionByLevelOrThrow(CoupleQuestionDomainService.FIRST_QUESTION_LEVEL);
                    TempCoupleQuestion tempQuestion = TempCoupleQuestion.create(MemberId.of(command.getUserId()), firstQuestion);
                    return coupleQuestionCommandHelper.saveTempCoupleQuestion(tempQuestion);
                });

        return GetQuestionUseCase.GetQuestionResponse.builder()
                .coupleQuestionId(tempCoupleQuestion.getId())
                .title(tempCoupleQuestion.getQuestion().getTitle())
                .content(tempCoupleQuestion.getQuestion().getContent())
                .meAnswered(tempCoupleQuestion.isAnswered())
                .level(CoupleQuestionDomainService.FIRST_QUESTION_LEVEL)
                .partnerAnswered(false)
                .createdAt(tempCoupleQuestion.getCreatedAt())
                .build();
    }

    @Override
    public GetQuestionAnswerUseCase.AnswerResponseDto getQuestionAnswers(GetQuestionAnswerUseCase.GetQuestionAnswerCommand command) {
        // 커플이 아닌 사용자는 TempCoupleQuestion의 답변을 조회
        Member member = memberQueryHelper.getMemberByIdOrThrow(MemberId.of(command.getUserId()));
        TempCoupleQuestion tempCoupleQuestion = coupleQuestionQueryHelper.getTempCoupleQuestionOrThrow(MemberId.of(command.getUserId()));

        return GetQuestionAnswerUseCase.AnswerResponseDto.builder()
                .title(tempCoupleQuestion.getQuestion().getTitle())
                .content(tempCoupleQuestion.getQuestion().getContent())
                .level(CoupleQuestionDomainService.FIRST_QUESTION_LEVEL)
                .createdAt(tempCoupleQuestion.getCreatedAt())
                .me(
                        GetQuestionAnswerUseCase.AnswerDto.builder()
                                .nickname(member.getNickname())
                                .answer(tempCoupleQuestion.getAnswer())
                                .updatable(true)
                                .build()
                )
                .partner(null) // 커플이 아닌 경우 파트너 답변은 없음
                .build();
    }

    @Override
    public AnswerQuestionUseCase.QuestionAnswerResponse answerQuestion(AnswerQuestionUseCase.AnswerQuestionCommand command) {
        // 커플이 아닌 사용자는 TempCoupleQuestion에 답변
        TempCoupleQuestion tempCoupleQuestion = coupleQuestionQueryHelper.getTempCoupleQuestionOrThrow(MemberId.of(command.getUserId()));

        // 답변을 저장
        if (tempCoupleQuestion.isAnswered()) {
            throw new MemberAccessDeniedException("이미 답변한 질문입니다.");
        }
        tempCoupleQuestion.answerQuestion(command.getAnswer());

        coupleQuestionCommandHelper.saveTempCoupleQuestion(tempCoupleQuestion);

        return AnswerQuestionUseCase.QuestionAnswerResponse.builder()
                .coupleQuestionId(tempCoupleQuestion.getId())
                .build();
    }

    @Override
    public AnswerQuestionUseCase.QuestionAnswerResponse updateAnswer(AnswerQuestionUseCase.AnswerQuestionCommand command) {
        // 커플이 아닌 사용자는 TempCoupleQuestion 답변 수정
        TempCoupleQuestion tempCoupleQuestion = coupleQuestionQueryHelper.getTempCoupleQuestionOrThrow(MemberId.of(command.getUserId()));

        // 답변을 수정
        if (!tempCoupleQuestion.isAnswered()) {
            throw new MemberAccessDeniedException("답변이 완료되지 않은 질문입니다.");
        }
        tempCoupleQuestion.updateAnswer(command.getAnswer());

        coupleQuestionCommandHelper.saveTempCoupleQuestion(tempCoupleQuestion);

        return AnswerQuestionUseCase.QuestionAnswerResponse.builder()
                .coupleQuestionId(tempCoupleQuestion.getId())
                .build();
    }
}
