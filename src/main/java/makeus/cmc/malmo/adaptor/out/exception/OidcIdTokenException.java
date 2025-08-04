package makeus.cmc.malmo.adaptor.out.exception;

public class OidcIdTokenException extends RuntimeException {
    public OidcIdTokenException(String message) {
        super(message);
    }

    public OidcIdTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
