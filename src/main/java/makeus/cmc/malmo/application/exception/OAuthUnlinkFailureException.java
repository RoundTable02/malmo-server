package makeus.cmc.malmo.application.exception;

public class OAuthUnlinkFailureException extends RuntimeException {
    public OAuthUnlinkFailureException(String message) {
        super(message);
    }
    public OAuthUnlinkFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
