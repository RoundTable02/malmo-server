package makeus.cmc.malmo.application.port.in.chat;

import lombok.Builder;
import lombok.Data;

public interface ProcessMessageUseCase {

    void processStreamChatMessage(ProcessMessageCommand command);
    void processSummary(ProcessSummaryCommand command);
    void processTotalSummary(ProcessTotalSummaryCommand command);
    void processAnswerMetadata(ProcessAnswerCommand command);

    @Data
    @Builder
    class ProcessMessageCommand {
        private Long memberId;
        private Long chatRoomId;
        private String nowMessage;
        private int promptLevel;
    }

    @Data
    @Builder
    class ProcessSummaryCommand {
        private Long chatRoomId;
        private Integer promptLevel;
    }

    @Data
    @Builder
    class ProcessTotalSummaryCommand {
        private Long chatRoomId;
    }

    @Data
    @Builder
    class ProcessAnswerCommand {
        private Long memberId;
        private Long coupleQuestionId;
    }
}
