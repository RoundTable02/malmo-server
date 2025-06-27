package makeus.cmc.malmo.application.port.out;

public interface ValidateOidcTokenPort {
    // OIDC 토큰을 검증하고 사용자의 providerId를 반환
    String validateKakao(String idToken);
}
