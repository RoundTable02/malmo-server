package makeus.cmc.malmo.application.exception;

public class NotValidCoupleCodeException extends RuntimeException {
    public NotValidCoupleCodeException() {
        super("유효하지 않은 초대 코드입니다.");
    }

    public NotValidCoupleCodeException(String message) {
        super(message);
    }
}
