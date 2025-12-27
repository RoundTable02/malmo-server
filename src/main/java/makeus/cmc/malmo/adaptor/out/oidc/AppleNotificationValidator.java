package makeus.cmc.malmo.adaptor.out.oidc;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.out.exception.OidcIdTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Apple Server-to-Server Notification JWT를 검증하고 파싱합니다.
 */
@Slf4j
@Component
public class AppleNotificationValidator {

    private final JwkProvider jwkProvider;
    private final ObjectMapper objectMapper;
    private final String expectedAudience;

    private static final String APPLE_ISS = "https://appleid.apple.com";
    private static final String JWKS_URI = "https://appleid.apple.com/auth/keys";

    /**
     * 프로덕션용 생성자 - Apple JWKS URI에서 키를 가져옵니다.
     */
    @Autowired
    public AppleNotificationValidator(
            ObjectMapper objectMapper,
            @Value("${apple.oidc.aud}") String expectedAudience) {
        try {
            this.jwkProvider = new JwkProviderBuilder(new URL(JWKS_URI))
                    .cached(10, 60, TimeUnit.MINUTES)
                    .rateLimited(10, 1, TimeUnit.MINUTES)
                    .build();
            this.objectMapper = objectMapper;
            this.expectedAudience = expectedAudience;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize AppleNotificationValidator", e);
        }
    }

    /**
     * 테스트용 생성자 - JwkProvider를 주입받습니다.
     * 주로 단위 테스트에서 mock을 주입할 때 사용합니다.
     */
    AppleNotificationValidator(
            JwkProvider jwkProvider,
            ObjectMapper objectMapper,
            String expectedAudience) {
        this.jwkProvider = jwkProvider;
        this.objectMapper = objectMapper;
        this.expectedAudience = expectedAudience;
    }

    /**
     * Apple Server-to-Server 알림 JWT를 검증하고 파싱합니다.
     *
     * @param signedPayload Apple이 보낸 JWT 형식의 서명된 페이로드
     * @return 파싱된 알림 클레임
     * @throws OidcIdTokenException 검증 실패 시
     */
    public AppleNotificationClaims validateAndParse(String signedPayload) {
        try {
            DecodedJWT jwt = JWT.decode(signedPayload);

            // 1. JWKS를 사용한 서명 검증
            Jwk jwk = jwkProvider.get(jwt.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            algorithm.verify(jwt);

            // 2. issuer 검증 (반드시 Apple이어야 함)
            if (!APPLE_ISS.equals(jwt.getIssuer())) {
                throw new OidcIdTokenException("Invalid issuer for Apple notification");
            }

            // 3. audience 검증 (앱의 client_id)
            if (!jwt.getAudience().contains(expectedAudience)) {
                throw new OidcIdTokenException("Invalid audience for Apple notification");
            }

            // 4. 만료 시간 검증
            if (jwt.getExpiresAt() != null && jwt.getExpiresAt().before(new Date())) {
                throw new OidcIdTokenException("Expired Apple notification token");
            }

            // 5. events 클레임 파싱
            String eventsPayload = jwt.getClaim("events").asString();
            JsonNode eventsNode = objectMapper.readTree(eventsPayload);

            return AppleNotificationClaims.builder()
                    .jti(jwt.getId())
                    .iat(jwt.getIssuedAt() != null ? jwt.getIssuedAt().getTime() : null)
                    .eventType(eventsNode.get("type").asText())
                    .sub(eventsNode.get("sub").asText())
                    .eventTime(eventsNode.has("event_time")
                            ? eventsNode.get("event_time").asLong() : null)
                    .email(eventsNode.has("email")
                            ? eventsNode.get("email").asText() : null)
                    .isPrivateEmail(eventsNode.has("is_private_email")
                            ? eventsNode.get("is_private_email").asBoolean() : null)
                    .build();

        } catch (OidcIdTokenException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to validate Apple notification JWT: {}", e.getMessage(), e);
            throw new OidcIdTokenException("Failed to validate Apple notification", e);
        }
    }

    /**
     * Apple 알림에서 파싱된 클레임
     */
    @Getter
    @Builder
    public static class AppleNotificationClaims {
        private final String jti;           // JWT ID (중복 방지용)
        private final Long iat;             // 발행 시간
        private final String eventType;     // email-enabled, consent-revoked, account-delete 등
        private final String sub;           // 사용자의 Apple providerId
        private final Long eventTime;       // 이벤트 발생 시간
        private final String email;         // 변경된 이메일 (있는 경우)
        private final Boolean isPrivateEmail;
    }
}

