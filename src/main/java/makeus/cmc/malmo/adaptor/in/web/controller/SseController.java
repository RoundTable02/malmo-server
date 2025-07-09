package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.ConnectSseUseCase;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "SSE Connection API", description = "Server-Sent Event(SSE) 연결 API")
@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

    private final ConnectSseUseCase connectSseUseCase;

    @Operation(
            summary = "SSE 연결",
            description = "사용자의 SSE 연결을 설정합니다. JWT 토큰이 필요합니다. BaseResponse를 사용하지 않습니다."
    )
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connectSse(@AuthenticationPrincipal User user) {
        ConnectSseUseCase.SseConnectionCommand command = ConnectSseUseCase.SseConnectionCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .build();
        return connectSseUseCase.connectSse(command).getSseEmitter();
    }

}
