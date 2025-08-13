package makeus.cmc.malmo.application.service.question.strategy;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionStrategyProvider {

    private final MemberQueryHelper memberQueryHelper;
    private final CoupleQuestionStrategy coupleQuestionStrategy;
    private final SingleQuestionStrategy singleQuestionStrategy;

    public QuestionHandlingStrategy getStrategy(MemberId memberId) {
        if (memberQueryHelper.isMemberCoupled(memberId)) {
            return coupleQuestionStrategy;
        }
        return singleQuestionStrategy;
    }
}
