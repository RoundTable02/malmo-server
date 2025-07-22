package makeus.cmc.malmo.domain.exception;

public class NotCoupleMemberException extends RuntimeException {
    public NotCoupleMemberException() {
        super("해당 회원은 커플이 아닙니다.");
    }
    public NotCoupleMemberException(String message) {
        super(message);
    }
}
