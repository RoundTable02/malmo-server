package makeus.cmc.malmo.application.port.out;

public interface ValidateTokenPort {
    boolean validateToken(String token);
    String getMemberIdFromToken(String token);
    String getMemberRoleFromToken(String token);
}