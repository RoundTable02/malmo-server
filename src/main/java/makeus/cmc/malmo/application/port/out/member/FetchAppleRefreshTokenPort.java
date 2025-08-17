package makeus.cmc.malmo.application.port.out.member;

public interface FetchAppleRefreshTokenPort {
    String getAppleRefreshToken(String authorizationCode);
}
