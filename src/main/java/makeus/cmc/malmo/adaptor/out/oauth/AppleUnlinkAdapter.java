package makeus.cmc.malmo.adaptor.out.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.application.exception.OAuthUnlinkFailureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleUnlinkAdapter {

    @Value("${apple.oauth.client-id}")
    private String clientId;

    @Value("${apple.oauth.revoke-uri}")
    private String appleRevokeUrl;

    private final AppleClientSecretGenerator clientSecretGenerator;

    public void revoke(String oauthToken) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            String clientSecret = clientSecretGenerator.generateClientSecret();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("token", oauthToken);
            body.add("token_type_hint", "refresh_token");

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    appleRevokeUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Apple token revoked successfully.");
            }

        } catch (HttpClientErrorException e) {
            log.error("Apple revoke failed: {}", e.getResponseBodyAsString());
            throw new OAuthUnlinkFailureException("Failed to revoke Apple token.", e);
        } catch (Exception e) {
            // JWT 생성 실패 등 다른 예외 처리
            log.error("An error occurred during Apple token revocation: {}", e.getMessage());
            throw new OAuthUnlinkFailureException("An error occurred during Apple token revocation.", e);
        }
    }
}
