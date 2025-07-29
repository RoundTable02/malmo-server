package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public interface GetQuestionAnswerUseCase {

    AnswerResponseDto getQuestionAnswers(GetQuestionAnswerCommand command);

    @Data
    @Builder
    class GetQuestionAnswerCommand {
        private Long userId;
        private Long coupleQuestionId;
    }

    @Data
    @Builder
    class AnswerResponseDto {
        private String title;
        private String content;
        private int level;
        private LocalDateTime createdAt;
        private AnswerDto me;
        private AnswerDto partner;
    }

    @Data
    @Builder
    class AnswerDto {
        private String nickname;
        private String answer;
        private boolean updatable;
    }
}
