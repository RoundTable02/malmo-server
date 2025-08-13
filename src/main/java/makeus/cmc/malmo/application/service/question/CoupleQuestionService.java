package makeus.cmc.malmo.application.service.question;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.aop.CheckCoupleMember;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.helper.couple.CoupleQueryHelper;
import makeus.cmc.malmo.application.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.application.port.in.question.AnswerQuestionUseCase;
import makeus.cmc.malmo.application.port.in.question.GetQuestionAnswerUseCase;
import makeus.cmc.malmo.application.port.in.question.GetQuestionUseCase;
import makeus.cmc.malmo.application.service.question.strategy.QuestionStrategyProvider;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoupleQuestionService implements GetQuestionUseCase, GetQuestionAnswerUseCase, AnswerQuestionUseCase {

    private final CoupleQueryHelper coupleQueryHelper;

    private final CoupleQuestionQueryHelper coupleQuestionQueryHelper;

    private final QuestionStrategyProvider questionStrategyProvider;

    @Override
    @CheckValidMember
    @Transactional
    public GetQuestionResponse getTodayQuestion(GetTodayQuestionCommand command) {
        return questionStrategyProvider.getStrategy(MemberId.of(command.getUserId()))
                .getTodayQuestion(command);
    }

    @Override
    @CheckValidMember
    public AnswerResponseDto getQuestionAnswers(GetQuestionAnswerCommand command) {
        return questionStrategyProvider.getStrategy(MemberId.of(command.getUserId()))
                .getQuestionAnswers(command);
    }

    @Override
    @CheckValidMember
    @Transactional
    public QuestionAnswerResponse answerQuestion(AnswerQuestionCommand command) {
        return questionStrategyProvider.getStrategy(MemberId.of(command.getUserId()))
                .answerQuestion(command);
    }

    @Override
    @CheckValidMember
    @Transactional
    public QuestionAnswerResponse updateAnswer(AnswerQuestionCommand command) {
        return questionStrategyProvider.getStrategy(MemberId.of(command.getUserId()))
                .updateAnswer(command);
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
}
