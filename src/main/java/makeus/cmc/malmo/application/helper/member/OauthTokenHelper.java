package makeus.cmc.malmo.application.helper.member;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.member.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OauthTokenHelper {

    private final KakaoIdTokenPort kakaoIdTokenPort;
    private final AppleIdTokenPort appleIdTokenPort;

    private final FetchFromOAuthProviderPort fetchFromOAuthProviderPort;
    private final FetchAppleRefreshTokenPort fetchAppleRefreshTokenPort;

    private final UnlinkApplePort unlinkApplePort;
    private final UnlinkKakaoPort unlinkKakaoPort;

    public String getKakaoIdTokenOrThrow(String idToken) {
        return kakaoIdTokenPort.validateToken(idToken);
    }

    public String getAppleIdTokenOrThrow(String idToken) {
        return appleIdTokenPort.validateToken(idToken);
    }

    public String fetchKakaoEmailOrThrow(String accessToken) {
        return fetchFromOAuthProviderPort.fetchMemberEmailFromKakao(accessToken);
    }

    public String getAppleEmailOrThrow(String idToken) {
        return appleIdTokenPort.extractEmailFromIdToken(idToken);
    }

    public String fetchAppleRefreshTokenOrThrow(String authorizationCode) {
        return fetchAppleRefreshTokenPort.getAppleRefreshToken(authorizationCode);
    }

    public void unlinkApple(String oauthToken) {
        unlinkApplePort.unlink(oauthToken);
    }

    public void unlinkKakao(String providerId) {
        unlinkKakaoPort.unlink(providerId);
    }

}
