package makeus.cmc.malmo.application.service.helper.question;

import lombok.*;
import makeus.cmc.malmo.application.port.out.question.LoadCoupleQuestionPort;
import makeus.cmc.malmo.application.port.out.question.LoadMemberAnswerPort;
import makeus.cmc.malmo.application.port.out.question.LoadQuestionPort;
import makeus.cmc.malmo.application.port.out.question.LoadTempCoupleQuestionPort;
import makeus.cmc.malmo.domain.exception.CoupleQuestionNotFoundException;
import makeus.cmc.malmo.domain.exception.MemberAccessDeniedException;
import makeus.cmc.malmo.domain.exception.QuestionNotFoundException;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.model.question.Question;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoupleQuestionQueryHelper {

    private final LoadCoupleQuestionPort loadCoupleQuestionPort;
    private final LoadTempCoupleQuestionPort loadTempCoupleQuestionPort;
    private final LoadMemberAnswerPort loadMemberAnswerPort;
    private final LoadQuestionPort loadQuestionPort;

    public CoupleQuestion getMaxLevelQuestionOrThrow(CoupleId coupleId) {
        return loadCoupleQuestionPort.loadMaxLevelCoupleQuestion(coupleId)
                .orElseThrow(CoupleQuestionNotFoundException::new);
    }

    public CoupleQuestionDto getMaxLevelQuestionDto(MemberId memberId, CoupleId coupleId) {
        return loadCoupleQuestionPort.getMaxLevelQuestionDto(memberId, coupleId)
                .orElseThrow(CoupleQuestionNotFoundException::new);
    }

    public CoupleQuestionDto getCoupleQuestionDtoByLevel(MemberId memberId, CoupleId coupleId, int level) {
        return loadCoupleQuestionPort.getCoupleQuestionDtoByLevel(memberId, coupleId, level)
                .orElseThrow(CoupleQuestionNotFoundException::new);
    }

    public Question getQuestionByLevelOrThrow(int level) {
        return loadQuestionPort.loadQuestionByLevel(level)
                .orElseThrow(QuestionNotFoundException::new);
    }

    public Optional<TempCoupleQuestion> getTempCoupleQuestion(MemberId memberId) {
        return loadTempCoupleQuestionPort.loadTempCoupleQuestionByMemberId(memberId);
    }

    public TempCoupleQuestion getTempCoupleQuestionOrThrow(MemberId memberId) {
        return loadTempCoupleQuestionPort.loadTempCoupleQuestionByMemberId(memberId)
                .orElseThrow(CoupleQuestionNotFoundException::new);
    }

    public MemberAnswer getMemberAnswerOrThrow(CoupleQuestionId coupleQuestionId, MemberId memberId) {
        return loadMemberAnswerPort.getMemberAnswer(coupleQuestionId, memberId)
                .orElseThrow(() -> new MemberAccessDeniedException("답변이 존재하지 않습니다."));
    }

    public void validateQuestionOwnership(CoupleQuestionId coupleQuestionId, CoupleId coupleId) {
        CoupleQuestion coupleQuestion = loadCoupleQuestionPort.loadCoupleQuestionById(coupleQuestionId)
                .orElseThrow(CoupleQuestionNotFoundException::new);

        if (!coupleQuestion.isOwnedBy(coupleId)) {
            throw new MemberAccessDeniedException("이 질문에 대한 권한이 없습니다.");
        }
    }

    public void validateMemberAlreadyAnswered(CoupleQuestionId coupleQuestionId, MemberId memberId) {
        boolean answered = loadMemberAnswerPort.isMemberAnswered(coupleQuestionId, memberId);
        if (answered) {
            throw new MemberAccessDeniedException("이미 답변한 질문입니다.");
        }
    }

    public MemberAnswersDto getQuestionAnswers(MemberId memberId, CoupleQuestionId coupleQuestionId) {
        return loadMemberAnswerPort.getQuestionAnswers(memberId, coupleQuestionId)
                .orElse(new MemberAnswersDto());
    }

    public long countAnswers(CoupleQuestionId coupleQuestionId) {
        return loadMemberAnswerPort.countAnswers(coupleQuestionId);
    }

    @Data
    @Builder
    public static class CoupleQuestionDto {
        private Long id;
        private String title;
        private String content;
        private int level;
        private CoupleId coupleId;
        private CoupleQuestionState coupleQuestionState;
        private LocalDateTime bothAnsweredAt;
        private boolean meAnswered;
        private boolean partnerAnswered;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberAnswersDto {
        private String title;
        private String content;
        private Integer level;
        private LocalDateTime createdAt;
        private AnswerDto me;
        private AnswerDto partner;
    }

    @Data
    @Builder
    public static class AnswerDto {
        private String nickname;
        private String answer;
        private boolean updatable;
    }
}
