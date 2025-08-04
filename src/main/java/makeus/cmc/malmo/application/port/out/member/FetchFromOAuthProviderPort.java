package makeus.cmc.malmo.application.port.out.member;

public interface FetchFromOAuthProviderPort {
    String fetchMemberEmailFromKakao(String accessToken);
}
