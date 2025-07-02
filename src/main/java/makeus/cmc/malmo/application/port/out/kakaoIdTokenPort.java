package makeus.cmc.malmo.application.port.out;

public interface kakaoIdTokenPort {
    // OIDC 토큰을 검증하고 사용자의 providerId를 반환
    String validateToken(String idToken);
}
