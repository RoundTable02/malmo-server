package makeus.cmc.malmo.adaptor.out.amplitude;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.port.out.amplitude.AmplitudePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmplitudeAdapter implements AmplitudePort {

    private final RestTemplate restTemplate;

    @Value("${amplitude.api.key}")
    private String apiKey;

    @Value("${amplitude.api.url}")
    private String apiUrl;

    @Override
    public void identifyUser(IdentifyUserCommand command) {
        try {
            AmplitudeRequest request = AmplitudeRequest.builder()
                    .apiKey(apiKey)
                    .identification(AmplitudeIdentification.builder()
                            .userId(command.getUserId())
                            .deviceId(command.getDeviceId())
                            .userProperties(UserProperties.builder()
                                    .email(command.getEmail())
                                    .nickname(command.getNickname())
                                    .build())
                            .build())
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("api_key", request.getApiKey());
            body.add("identification", "[{\"user_id\":\"" + request.getIdentification().getUserId() + 
                    "\",\"device_id\":\"" + request.getIdentification().getDeviceId() + 
                    "\",\"user_properties\":{\"email\":\"" + request.getIdentification().getUserProperties().getEmail() + 
                    "\",\"nickname\":\"" + request.getIdentification().getUserProperties().getNickname() + "\"}}]");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Amplitude identify API 호출 성공: userId={}, deviceId={}", 
                        command.getUserId(), command.getDeviceId());
            } else {
                log.warn("Amplitude identify API 호출 실패: status={}, userId={}", 
                        response.getStatusCode(), command.getUserId());
            }
        } catch (Exception e) {
            log.error("Amplitude identify API 호출 중 오류 발생: userId={}, error={}", 
                    command.getUserId(), e.getMessage(), e);
        }
    }

    @Data
    @Builder
    private static class AmplitudeRequest {
        private String apiKey;
        private AmplitudeIdentification identification;
    }

    @Data
    @Builder
    private static class AmplitudeIdentification {
        private String userId;
        private String deviceId;
        private UserProperties userProperties;
    }

    @Data
    @Builder
    private static class UserProperties {
        private String email;
        private String nickname;
    }
}
