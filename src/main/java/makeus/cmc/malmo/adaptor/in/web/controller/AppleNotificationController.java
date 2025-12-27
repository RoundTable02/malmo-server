package makeus.cmc.malmo.adaptor.in.web.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.port.in.member.AppleNotificationUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Apple Sign in with Apple Server-to-Server 알림을 처리하는 Webhook 컨트롤러
 *
 * @see <a href="https://developer.apple.com/documentation/sign_in_with_apple/processing_changes_for_sign_in_with_apple_accounts">Apple Documentation</a>
 */
@Slf4j
@Tag(name = "Apple Webhook", description = "Apple Server-to-Server 알림 처리")
@RestController
@RequestMapping("/webhook/apple")
@RequiredArgsConstructor
public class AppleNotificationController {

    private final AppleNotificationUseCase appleNotificationUseCase;

    /**
     * JSON 형식의 Apple 알림을 처리합니다.
     * Content-Type: application/json
     */
    @Operation(
            summary = "Apple Server-to-Server 알림 수신 (JSON)",
            description = "Sign in with Apple 계정 변경 알림을 JSON 형식으로 처리합니다."
    )
    @PostMapping(value = "/notifications", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> handleNotificationJson(
            @RequestBody AppleNotificationRequest request
    ) {
        return processPayload(request.getSignedPayload());
    }

    /**
     * Form URL Encoded 형식의 Apple 알림을 처리합니다.
     * Content-Type: application/x-www-form-urlencoded
     */
    @Operation(
            summary = "Apple Server-to-Server 알림 수신 (Form)",
            description = "Sign in with Apple 계정 변경 알림을 Form 형식으로 처리합니다."
    )
    @PostMapping(value = "/notifications", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> handleNotificationForm(
            @RequestParam("signed_payload") String signedPayload
    ) {
        return processPayload(signedPayload);
    }

    /**
     * 공통 페이로드 처리 로직
     * Apple의 재시도를 방지하기 위해 항상 200 OK를 반환합니다.
     */
    private ResponseEntity<Void> processPayload(String signedPayload) {
        if (signedPayload == null || signedPayload.isBlank()) {
            log.warn("Apple notification received with empty payload");
            return ResponseEntity.ok().build();
        }

        try {
            log.info("Processing Apple notification");
            appleNotificationUseCase.processNotification(signedPayload);
        } catch (Exception e) {
            // Apple의 재시도를 방지하기 위해 예외가 발생해도 200 OK 반환
            log.error("Failed to process Apple notification: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok().build();
    }

    @Data
    public static class AppleNotificationRequest {
        @JsonProperty("signed_payload")
        private String signedPayload;
    }
}



