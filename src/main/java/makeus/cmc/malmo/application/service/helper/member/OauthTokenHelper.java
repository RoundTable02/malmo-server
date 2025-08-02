package makeus.cmc.malmo.application.service.helper.member;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.AppleIdTokenPort;
import makeus.cmc.malmo.application.port.out.FetchFromOAuthProviderPort;
import makeus.cmc.malmo.application.port.out.kakaoIdTokenPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OauthTokenHelper {

    private final kakaoIdTokenPort kakaoIdTokenPort;
    private final AppleIdTokenPort appleIdTokenPort;

    private final FetchFromOAuthProviderPort fetchFromOAuthProviderPort;

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

}
