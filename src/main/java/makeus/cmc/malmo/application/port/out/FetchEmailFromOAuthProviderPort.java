package makeus.cmc.malmo.application.port.out;

public interface FetchEmailFromOAuthProviderPort {
    String fetchEmailFromKakaoIdToken(String accessToken);
}
