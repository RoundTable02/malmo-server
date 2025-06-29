package makeus.cmc.malmo.adaptor.out.oidc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.oidc.exception.RestApiException;
import makeus.cmc.malmo.application.port.out.FetchEmailFromOAuthProviderPort;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class KakaoRestApiAdaptor implements FetchEmailFromOAuthProviderPort {

    private final ObjectMapper objectMapper;

    @Override
    public String fetchEmailFromKakaoIdToken(String accessToken) {
        String url = "https://kapi.kakao.com/v1/oidc/userinfo";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RestApiException("카카오 OIDC 사용자 정보 조회 실패");
        }

        try {
            JsonNode root = objectMapper.readTree(response.getBody());

            JsonNode emailNode = root.path("email");
            boolean isEmailVerified = root.path("email_verified").asBoolean(false);

            if (emailNode.isMissingNode() || !isEmailVerified) {
                throw new RestApiException("이메일이 없거나 인증되지 않았습니다.");
            }

            return emailNode.asText();

        } catch (Exception e) {
            throw new RestApiException("카카오 OIDC 사용자 정보 파싱 실패", e);
        }
    }
}
