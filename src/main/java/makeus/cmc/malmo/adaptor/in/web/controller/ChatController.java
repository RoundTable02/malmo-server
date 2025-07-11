package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.SendChatMessageUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "채팅 전송 API", description = "사용자 채팅 전송을 위한 API")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final SendChatMessageUseCase sendChatMessageUseCase;

    @PostMapping("/send")
    public void sendChatMessage(
            @AuthenticationPrincipal User user,
            ChatRequest request) {
        sendChatMessageUseCase.processUserMessage(
                SendChatMessageUseCase.SendChatMessageCommand.builder()
                        .userId(Long.valueOf(user.getUsername()))
                        .message(request.getMessage())
                        .build()
        );
    }

    @Getter
    @AllArgsConstructor
    public class ChatRequest {
        private String message;
    }
}
