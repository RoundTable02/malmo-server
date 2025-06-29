package makeus.cmc.malmo.adaptor.out.oidc.exception;

public class RestApiException extends RuntimeException {
    public RestApiException(String message) {
        super(message);
    }

    public RestApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
