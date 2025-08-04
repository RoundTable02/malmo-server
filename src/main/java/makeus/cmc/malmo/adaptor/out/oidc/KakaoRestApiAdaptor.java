package makeus.cmc.malmo.adaptor.out.oidc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.oidc.exception.RestApiException;
import makeus.cmc.malmo.application.port.out.member.FetchFromOAuthProviderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class KakaoRestApiAdaptor implements FetchFromOAuthProviderPort {

    private final ObjectMapper objectMapper;

    @Value("${kakao.oidc.user-info-uri}")
    private String url;

    @Override
    public String fetchMemberEmailFromKakao(String accessToken) {
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

            String email = null;
            if (!emailNode.isMissingNode() && isEmailVerified) {
                email = emailNode.asText();
            }

            return email;

        } catch (Exception e) {
            throw new RestApiException("카카오 OIDC 사용자 정보 파싱 실패", e);
        }
    }
}
