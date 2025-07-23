package makeus.cmc.malmo.domain.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("Member not found");
    }
    public MemberNotFoundException(String message) {
        super(message);
    }
}
