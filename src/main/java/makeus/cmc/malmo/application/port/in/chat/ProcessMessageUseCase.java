package makeus.cmc.malmo.application.port.in.chat;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.CompletableFuture;

public interface ProcessMessageUseCase {

    CompletableFuture<Void> processStreamChatMessage(ProcessMessageCommand command);
    CompletableFuture<Void> processSummary(ProcessSummaryCommand command);
    CompletableFuture<Void> processTotalSummary(ProcessTotalSummaryCommand command);
    CompletableFuture<Void> processAnswerMetadata(ProcessAnswerCommand command);

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
        private Long coupleId;
        private Long memberId;
        private Long coupleQuestionId;
    }
}
