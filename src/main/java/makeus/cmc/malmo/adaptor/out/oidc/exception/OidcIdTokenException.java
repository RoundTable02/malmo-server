package makeus.cmc.malmo.adaptor.out.oidc.exception;

public class OidcIdTokenException extends RuntimeException {
    public OidcIdTokenException(String message) {
        super(message);
    }

    public OidcIdTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
