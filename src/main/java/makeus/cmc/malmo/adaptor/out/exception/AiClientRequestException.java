package makeus.cmc.malmo.adaptor.out.exception;

public class AiClientRequestException extends RuntimeException {
    public AiClientRequestException(String message) {
        super(message);
    }
    public AiClientRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
