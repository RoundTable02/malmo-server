package makeus.cmc.malmo.application.exception;

public class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException() {
        super("메시지가 존재하지 않습니다.");
    }

    public MessageNotFoundException(String message) {
        super(message);
    }
}
