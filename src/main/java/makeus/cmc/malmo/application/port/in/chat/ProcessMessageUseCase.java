package makeus.cmc.malmo.application.port.in.chat;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.CompletableFuture;

public interface ProcessMessageUseCase {

    CompletableFuture<Void> processStreamChatMessage(ProcessMessageCommand command);
    CompletableFuture<Void> processAnswerMetadata(ProcessAnswerCommand command);
    
    // 제목 생성 처리
    CompletableFuture<Void> processTitleGeneration(ProcessTitleGenerationCommand command);

    @Data
    @Builder
    class ProcessMessageCommand {
        private Long memberId;
        private Long chatRoomId;
        private String nowMessage;
        private int promptLevel;
        private int detailedLevel;
    }

    @Data
    @Builder
    class ProcessAnswerCommand {
        private Long coupleId;
        private Long memberId;
        private Long coupleQuestionId;
    }

    @Data
    @Builder
    class ProcessTitleGenerationCommand {
        private Long chatRoomId;
    }
}
