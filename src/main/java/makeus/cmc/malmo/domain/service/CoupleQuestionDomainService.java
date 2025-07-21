package makeus.cmc.malmo.domain.service;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.*;
import makeus.cmc.malmo.domain.exception.CoupleQuestionNotFoundException;
import makeus.cmc.malmo.domain.exception.MemberAccessDeniedException;
import makeus.cmc.malmo.domain.exception.QuestionNotFoundException;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.model.question.Question;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.CoupleMemberId;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class CoupleQuestionDomainService {

    private final LoadCoupleQuestionPort loadCoupleQuestionPort;
    private final LoadTempCoupleQuestionPort loadTempCoupleQuestionPort;
    private final LoadQuestionPort loadQuestionPort;
    private final LoadMemberAnswerPort loadMemberAnswerPort;
    private final LoadCouplePort loadCouplePort;

    private final SaveCoupleQuestionPort saveCoupleQuestionPort;
    private final SaveTempCoupleQuestionPort saveTempCoupleQuestionPort;

    public static final int FIRST_QUESTION_LEVEL = 1;

    public CoupleQuestion getMaxLevelQuestion(CoupleId coupleId) {
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

    public TempCoupleQuestion getTempCoupleQuestion(MemberId memberId) {
        return loadTempCoupleQuestionPort.loadTempCoupleQuestionByMemberId(memberId)
                .orElseGet(() -> {
                            Question question = loadQuestionPort.loadQuestionByLevel(FIRST_QUESTION_LEVEL)
                                    .orElseThrow(QuestionNotFoundException::new);
                            TempCoupleQuestion tempCoupleQuestion = TempCoupleQuestion.create(memberId, question);
                            return saveTempCoupleQuestionPort.saveTempCoupleQuestion(tempCoupleQuestion);
                        }
                );
    }

    @Transactional
    public CoupleQuestion createNextCoupleQuestion(CoupleId coupleId, int nowLevel) {
        Question nextQuestion = loadQuestionPort.loadQuestionByLevel(nowLevel + 1)
                .orElseThrow(QuestionNotFoundException::new);
        CoupleQuestion coupleQuestion = CoupleQuestion.createCoupleQuestion(nextQuestion, coupleId);

        return saveCoupleQuestionPort.saveCoupleQuestion(coupleQuestion);
    }

    public void validateQuestionOwnership(CoupleQuestionId coupleQuestionId, CoupleId coupleId) {
        CoupleQuestion coupleQuestion = loadCoupleQuestionPort.loadCoupleQuestionById(coupleQuestionId)
                .orElseThrow(CoupleQuestionNotFoundException::new);

        if (!coupleQuestion.isOwnedBy(coupleId)) {
            throw new MemberAccessDeniedException("이 질문에 대한 권한이 없습니다.");
        }
    }

    public MemberAnswersDto getQuestionAnswers(CoupleQuestionId coupleQuestionId) {
        return loadMemberAnswerPort.getQuestionAnswers(coupleQuestionId)
                .orElse(new MemberAnswersDto());
    }

    @Transactional
    public void answerQuestion(CoupleQuestion coupleQuestion, MemberId memberId, String answer) {
        CoupleMemberId coupleMemberId = loadCouplePort.loadCoupleMemberIdByMemberId(memberId);
        // 이미 답변한 질문인지 확인
        boolean answered = loadMemberAnswerPort.isMemberAnswered(CoupleQuestionId.of(coupleQuestion.getId()), memberId);
        if (answered) {
            throw new MemberAccessDeniedException("이미 답변한 질문입니다.");
        }
        MemberAnswer memberAnswer = coupleQuestion.createMemberAnswer(coupleMemberId, answer);
        saveCoupleQuestionPort.saveMemberAnswer(memberAnswer);
    }

    @Transactional
    public void answerQuestion(TempCoupleQuestion coupleQuestion, String answer) {
        if (coupleQuestion.isAnswered()) {
            throw new MemberAccessDeniedException("이미 답변한 질문입니다.");
        }

        coupleQuestion.answerQuestion(answer);
        saveTempCoupleQuestionPort.saveTempCoupleQuestion(coupleQuestion);
    }

    public long countAnswers(CoupleQuestionId coupleQuestionId) {
        return loadMemberAnswerPort.countAnswers(coupleQuestionId);
    }

    @Transactional
    public void updateQuestionComplete(CoupleQuestion coupleQuestion) {
        coupleQuestion.complete();
        saveCoupleQuestionPort.saveCoupleQuestion(coupleQuestion);
    }

    @Transactional
    public void updateAnswer(CoupleQuestion coupleQuestion, MemberId memberId, String answer) {
        if (!coupleQuestion.isUpdatable()) {
            throw new MemberAccessDeniedException("이미 답변이 완료된 질문입니다.");
        }
        MemberAnswer memberAnswer = loadMemberAnswerPort.getMemberAnswer(CoupleQuestionId.of(coupleQuestion.getId()), memberId)
                .orElseThrow(() -> new MemberAccessDeniedException("답변이 존재하지 않습니다."));

        memberAnswer.updateAnswer(answer);
        saveCoupleQuestionPort.saveMemberAnswer(memberAnswer);
    }

    @Transactional
    public void updateAnswer(TempCoupleQuestion coupleQuestion, String answer) {
        if (!coupleQuestion.isAnswered()) {
            throw new MemberAccessDeniedException("답변이 완료되지 않은 질문입니다.");
        }

        coupleQuestion.answerQuestion(answer);
        saveTempCoupleQuestionPort.saveTempCoupleQuestion(coupleQuestion);
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
    public class MemberAnswersDto {
        private AnswerDto me;
        private AnswerDto partner;
    }

    @Data
    public class AnswerDto {
        private String nickname;
        private String answer;
        private boolean updatable;
    }


    public boolean needsNextQuestion(LocalDateTime bothAnsweredAt) {
        if (bothAnsweredAt == null) {
            return false;
        }

        LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();

        return bothAnsweredAt.toLocalDate().equals(yesterday);
    }
}
