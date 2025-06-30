package makeus.cmc.malmo.adaptor.out.oidc;

import makeus.cmc.malmo.application.port.out.kakaoIdTokenPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoOidcAdapter extends AbstractOidcAdapter implements kakaoIdTokenPort {

    public KakaoOidcAdapter(
            @Value("${kakao.oidc.iss}") String iss,
            @Value("${kakao.oidc.aud}") String aud,
            @Value("${kakao.oidc.jwks-uri}") String jwksUri) throws Exception {
        super(iss, aud, jwksUri);
    }
}
