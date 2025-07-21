package makeus.cmc.malmo.application.port.in;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public interface GetQuestionUseCase {

    GetQuestionResponse getTodayQuestion(GetTodayQuestionCommand command);


    @Data
    @Builder
    class GetTodayQuestionCommand {
        private Long userId;
    }


    @Data
    @Builder
    class GetQuestionResponse {
        private Long coupleQuestionId;
        private String title;
        private String content;
        private int level;
        private boolean meAnswered;
        private boolean partnerAnswered;
        private LocalDateTime createdAt;
    }
}
