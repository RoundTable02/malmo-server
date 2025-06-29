package makeus.cmc.malmo.application.port.out;

public interface FetchFromOAuthProviderPort {
    String fetchMemberEmailFromKakao(String accessToken);
}
