package makeus.cmc.malmo.adaptor.out.oidc;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import makeus.cmc.malmo.adaptor.out.exception.OidcIdTokenException;
import makeus.cmc.malmo.adaptor.out.oidc.AppleNotificationValidator.AppleNotificationClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppleNotificationValidator 단위 테스트")
class AppleNotificationValidatorTest {

    @Mock
    private JwkProvider jwkProvider;

    @Mock
    private Jwk jwk;

    private AppleNotificationValidator validator;
    private ObjectMapper objectMapper;

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    private static final String APPLE_ISS = "https://appleid.apple.com";
    private static final String EXPECTED_AUD = "com.malmo.app";
    private static final String KEY_ID = "test-key-id";

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        validator = new AppleNotificationValidator(jwkProvider, objectMapper, EXPECTED_AUD);

        // RSA 키 쌍 생성
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        publicKey = (RSAPublicKey) keyPair.getPublic();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
    }

    private String createValidSignedPayload(String eventType, String sub, Long eventTime) throws Exception {
        Map<String, Object> events = Map.of(
                "type", eventType,
                "sub", sub,
                "event_time", eventTime
        );
        String eventsJson = objectMapper.writeValueAsString(events);

        return JWT.create()
                .withIssuer(APPLE_ISS)
                .withAudience(EXPECTED_AUD)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
                .withJWTId(UUID.randomUUID().toString())
                .withKeyId(KEY_ID)
                .withClaim("events", eventsJson)
                .sign(Algorithm.RSA256(publicKey, privateKey));
    }

    @Nested
    @DisplayName("유효한 알림 검증 테스트")
    class ValidNotificationTest {

        @Test
        @DisplayName("consent-revoked 이벤트를 정상적으로 파싱한다")
        void consent_revoked_이벤트를_정상적으로_파싱한다() throws Exception {
            // given
            String sub = "000123.abcdef1234567890.1234";
            Long eventTime = Instant.now().getEpochSecond();
            String signedPayload = createValidSignedPayload("consent-revoked", sub, eventTime);

            given(jwkProvider.get(anyString())).willReturn(jwk);
            given(jwk.getPublicKey()).willReturn(publicKey);

            // when
            AppleNotificationClaims claims = validator.validateAndParse(signedPayload);

            // then
            assertThat(claims.getEventType()).isEqualTo("consent-revoked");
            assertThat(claims.getSub()).isEqualTo(sub);
            assertThat(claims.getEventTime()).isEqualTo(eventTime);
            assertThat(claims.getJti()).isNotNull();
        }

        @Test
        @DisplayName("account-delete 이벤트를 정상적으로 파싱한다")
        void account_delete_이벤트를_정상적으로_파싱한다() throws Exception {
            // given
            String sub = "000456.xyz9876543210.5678";
            Long eventTime = Instant.now().getEpochSecond();
            String signedPayload = createValidSignedPayload("account-delete", sub, eventTime);

            given(jwkProvider.get(anyString())).willReturn(jwk);
            given(jwk.getPublicKey()).willReturn(publicKey);

            // when
            AppleNotificationClaims claims = validator.validateAndParse(signedPayload);

            // then
            assertThat(claims.getEventType()).isEqualTo("account-delete");
            assertThat(claims.getSub()).isEqualTo(sub);
        }

        @Test
        @DisplayName("email-enabled 이벤트를 정상적으로 파싱한다")
        void email_enabled_이벤트를_정상적으로_파싱한다() throws Exception {
            // given
            String sub = "000789.email1234567890.9012";
            Long eventTime = Instant.now().getEpochSecond();
            String signedPayload = createValidSignedPayload("email-enabled", sub, eventTime);

            given(jwkProvider.get(anyString())).willReturn(jwk);
            given(jwk.getPublicKey()).willReturn(publicKey);

            // when
            AppleNotificationClaims claims = validator.validateAndParse(signedPayload);

            // then
            assertThat(claims.getEventType()).isEqualTo("email-enabled");
            assertThat(claims.getSub()).isEqualTo(sub);
        }
    }

    @Nested
    @DisplayName("유효하지 않은 알림 검증 테스트")
    class InvalidNotificationTest {

        @Test
        @DisplayName("issuer가 Apple이 아니면 예외를 던진다")
        void issuer가_Apple이_아니면_예외를_던진다() throws Exception {
            // given
            Map<String, Object> events = Map.of("type", "consent-revoked", "sub", "test-sub", "event_time", 123456789L);
            String eventsJson = objectMapper.writeValueAsString(events);

            String invalidPayload = JWT.create()
                    .withIssuer("https://invalid-issuer.com")  // 잘못된 issuer
                    .withAudience(EXPECTED_AUD)
                    .withIssuedAt(Date.from(Instant.now()))
                    .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
                    .withJWTId(UUID.randomUUID().toString())
                    .withKeyId(KEY_ID)
                    .withClaim("events", eventsJson)
                    .sign(Algorithm.RSA256(publicKey, privateKey));

            given(jwkProvider.get(anyString())).willReturn(jwk);
            given(jwk.getPublicKey()).willReturn(publicKey);

            // when & then
            assertThatThrownBy(() -> validator.validateAndParse(invalidPayload))
                    .isInstanceOf(OidcIdTokenException.class)
                    .hasMessageContaining("Invalid issuer");
        }

        @Test
        @DisplayName("audience가 앱 클라이언트 ID와 다르면 예외를 던진다")
        void audience가_앱_클라이언트_ID와_다르면_예외를_던진다() throws Exception {
            // given
            Map<String, Object> events = Map.of("type", "consent-revoked", "sub", "test-sub", "event_time", 123456789L);
            String eventsJson = objectMapper.writeValueAsString(events);

            String invalidPayload = JWT.create()
                    .withIssuer(APPLE_ISS)
                    .withAudience("com.wrong.app")  // 잘못된 audience
                    .withIssuedAt(Date.from(Instant.now()))
                    .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
                    .withJWTId(UUID.randomUUID().toString())
                    .withKeyId(KEY_ID)
                    .withClaim("events", eventsJson)
                    .sign(Algorithm.RSA256(publicKey, privateKey));

            given(jwkProvider.get(anyString())).willReturn(jwk);
            given(jwk.getPublicKey()).willReturn(publicKey);

            // when & then
            assertThatThrownBy(() -> validator.validateAndParse(invalidPayload))
                    .isInstanceOf(OidcIdTokenException.class)
                    .hasMessageContaining("Invalid audience");
        }

        @Test
        @DisplayName("만료된 토큰이면 예외를 던진다")
        void 만료된_토큰이면_예외를_던진다() throws Exception {
            // given
            Map<String, Object> events = Map.of("type", "consent-revoked", "sub", "test-sub", "event_time", 123456789L);
            String eventsJson = objectMapper.writeValueAsString(events);

            String expiredPayload = JWT.create()
                    .withIssuer(APPLE_ISS)
                    .withAudience(EXPECTED_AUD)
                    .withIssuedAt(Date.from(Instant.now().minusSeconds(7200)))
                    .withExpiresAt(Date.from(Instant.now().minusSeconds(3600)))  // 만료됨
                    .withJWTId(UUID.randomUUID().toString())
                    .withKeyId(KEY_ID)
                    .withClaim("events", eventsJson)
                    .sign(Algorithm.RSA256(publicKey, privateKey));

            given(jwkProvider.get(anyString())).willReturn(jwk);
            given(jwk.getPublicKey()).willReturn(publicKey);

            // when & then
            assertThatThrownBy(() -> validator.validateAndParse(expiredPayload))
                    .isInstanceOf(OidcIdTokenException.class)
                    .hasMessageContaining("Expired");
        }

        @Test
        @DisplayName("서명 검증에 실패하면 예외를 던진다")
        void 서명_검증에_실패하면_예외를_던진다() throws Exception {
            // given
            String validPayload = createValidSignedPayload("consent-revoked", "test-sub", 123456789L);

            // 다른 키로 검증 시도
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair differentKeyPair = keyGen.generateKeyPair();
            RSAPublicKey differentPublicKey = (RSAPublicKey) differentKeyPair.getPublic();

            given(jwkProvider.get(anyString())).willReturn(jwk);
            given(jwk.getPublicKey()).willReturn(differentPublicKey);  // 다른 공개키

            // when & then
            assertThatThrownBy(() -> validator.validateAndParse(validPayload))
                    .isInstanceOf(OidcIdTokenException.class);
        }

        @Test
        @DisplayName("유효하지 않은 JWT 형식이면 예외를 던진다")
        void 유효하지_않은_JWT_형식이면_예외를_던진다() {
            // given
            String invalidJwt = "this-is-not-a-valid-jwt";

            // when & then
            assertThatThrownBy(() -> validator.validateAndParse(invalidJwt))
                    .isInstanceOf(OidcIdTokenException.class);
        }
    }

    @Nested
    @DisplayName("이벤트 파싱 테스트")
    class EventsParsingTest {

        @Test
        @DisplayName("email 필드가 포함된 이벤트를 파싱한다")
        void email_필드가_포함된_이벤트를_파싱한다() throws Exception {
            // given
            Map<String, Object> events = Map.of(
                    "type", "email-enabled",
                    "sub", "test-sub",
                    "event_time", 123456789L,
                    "email", "user@privaterelay.appleid.com",
                    "is_private_email", true
            );
            String eventsJson = objectMapper.writeValueAsString(events);

            String signedPayload = JWT.create()
                    .withIssuer(APPLE_ISS)
                    .withAudience(EXPECTED_AUD)
                    .withIssuedAt(Date.from(Instant.now()))
                    .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
                    .withJWTId(UUID.randomUUID().toString())
                    .withKeyId(KEY_ID)
                    .withClaim("events", eventsJson)
                    .sign(Algorithm.RSA256(publicKey, privateKey));

            given(jwkProvider.get(anyString())).willReturn(jwk);
            given(jwk.getPublicKey()).willReturn(publicKey);

            // when
            AppleNotificationClaims claims = validator.validateAndParse(signedPayload);

            // then
            assertThat(claims.getEmail()).isEqualTo("user@privaterelay.appleid.com");
            assertThat(claims.getIsPrivateEmail()).isTrue();
        }
    }
}



