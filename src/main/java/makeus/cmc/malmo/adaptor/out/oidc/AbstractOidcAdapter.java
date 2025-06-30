package makeus.cmc.malmo.adaptor.out.oidc;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import makeus.cmc.malmo.adaptor.out.oidc.exception.OidcIdTokenException;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeUnit;

public abstract class AbstractOidcAdapter {

    protected final String iss; // 발급자
    protected final String aud; // 앱 클라이언트 ID
    protected final JwkProvider provider;

    protected AbstractOidcAdapter(String iss, String aud, String jwksUri) throws Exception {
        this.iss = iss;
        this.aud = aud;
        this.provider = new JwkProviderBuilder(new URL(jwksUri))
                .cached(10, 60, TimeUnit.MINUTES)
                .rateLimited(10, 1, TimeUnit.MINUTES)
                .build();
    }

    public String validateToken(String idToken) {
        try {
            DecodedJWT jwt = JWT.decode(idToken);

            // 1. 서명 검증
            Jwk jwk = provider.get(jwt.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            algorithm.verify(jwt);

            // 2. 발급자(iss), 대상(aud), 만료 시간(exp) 검증
            if (!jwt.getIssuer().equals(iss) || !jwt.getAudience().contains(aud)) {
                throw new OidcIdTokenException("Invalid OIDC Token");
            }

            if (jwt.getExpiresAt().before(new java.util.Date())) {
                throw new OidcIdTokenException("Expired OIDC Token");
            }

            // 3. 검증 성공 시 Provider Id 반환
            return jwt.getSubject();

        } catch (Exception e) {
            throw new OidcIdTokenException("Failed to validate OIDC Token", e);
        }
    }
}
