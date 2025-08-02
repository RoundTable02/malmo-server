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
import makeus.cmc.malmo.application.service.strategy.QuestionStrategyProvider;
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
