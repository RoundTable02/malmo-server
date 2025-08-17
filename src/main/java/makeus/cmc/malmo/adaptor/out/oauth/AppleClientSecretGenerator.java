package makeus.cmc.malmo.adaptor.out.oauth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class AppleClientSecretGenerator {

    @Value("${apple.oauth.team-id}")
    private String teamId;

    @Value("${apple.oauth.client-id}")
    private String clientId;

    @Value("${apple.oauth.key-id}")
    private String keyId;

    @Value("${apple.oauth.private-key}")
    private String privateKey;

    public String generateClientSecret() throws Exception {
        Date now = new Date();
        // Apple client_secret은 유효 기간이 6개월을 넘으면 안 됨
        Date expiration = new Date(now.getTime() + (1000L * 60 * 60 * 24 * 150)); // 약 5개월

        return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setHeaderParam("alg", "ES256")
                .setIssuer(teamId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setAudience("https://appleid.apple.com")
                .setSubject(clientId)
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() throws Exception {
        String privateKeyPem = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decodedKey = Base64.getDecoder().decode(privateKeyPem);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);
    }
}
