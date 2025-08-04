package makeus.cmc.malmo.adaptor.out.oidc;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import makeus.cmc.malmo.adaptor.out.exception.OidcIdTokenException;
import makeus.cmc.malmo.application.port.out.member.AppleIdTokenPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppleOidcAdapter extends AbstractOidcAdapter implements AppleIdTokenPort {

    public AppleOidcAdapter(
            @Value("${apple.oidc.iss}") String iss,
            @Value("${apple.oidc.aud}") String aud,
            @Value("${apple.oidc.jwks-uri}") String jwksUri) throws Exception {
        super(iss, aud, jwksUri);
    }

    public String extractEmailFromIdToken(String idToken) {
        try {
            DecodedJWT jwt = JWT.decode(idToken);
            return jwt.getClaim("email").asString();
        } catch (Exception e) {
            throw new OidcIdTokenException("Failed to extract email from Apple ID token", e);
        }
    }

}
