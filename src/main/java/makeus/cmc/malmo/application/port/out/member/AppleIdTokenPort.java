package makeus.cmc.malmo.application.port.out.member;

public interface AppleIdTokenPort {
    // OIDC 토큰을 검증하고 사용자의 providerId를 반환
    String validateToken(String idToken);
    String extractEmailFromIdToken(String idToken);
}
