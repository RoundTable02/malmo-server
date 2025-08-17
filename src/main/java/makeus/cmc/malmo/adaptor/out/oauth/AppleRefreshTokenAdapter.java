package makeus.cmc.malmo.adaptor.out.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.exception.RestApiException;
import makeus.cmc.malmo.application.port.out.member.FetchAppleRefreshTokenPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class AppleRefreshTokenAdapter implements FetchAppleRefreshTokenPort {

    private final AppleClientSecretGenerator clientSecretGenerator;

    @Value("${apple.oauth.client-id}")
    private String appleClientId;

    @Value("${apple.oauth.token-uri}")
    private String appleTokenUrl;

    @Override
    public String getAppleRefreshToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            // 1. client_secret 생성
            String clientSecret = clientSecretGenerator.generateClientSecret();

            // 2. HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 3. HTTP 요청 본문(파라미터) 설정
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", appleClientId);
            params.add("client_secret", clientSecret);
            params.add("code", authorizationCode);
            params.add("grant_type", "authorization_code"); // 고정값

            // 4. HttpEntity 생성
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // 5. RestTemplate으로 POST 요청 보내기
            AppleTokenResponse response = restTemplate.postForObject(appleTokenUrl, request, AppleTokenResponse.class);

            return response.getRefresh_token();

        } catch (Exception e) {
            // 예외 처리 (e.g., 로깅, 커스텀 예외 발생)
            throw new RestApiException("Apple 토큰을 발급받는 중 오류가 발생했습니다.", e);
        }
    }

    // Apple 서버로부터 받을 토큰 응답 DTO
    @Getter
    @NoArgsConstructor
    public static class AppleTokenResponse {
        private String access_token;
        private Long expires_in;
        private String id_token;
        private String refresh_token;
        private String token_type;
    }
}
