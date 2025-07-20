package makeus.cmc.malmo.domain.exception;

public class MemberAccessDeniedException extends RuntimeException {
    public MemberAccessDeniedException() {
        super("리소스 접근 권한이 없습니다.");
    }

    public MemberAccessDeniedException(String message) {
        super(message);
    }
}
