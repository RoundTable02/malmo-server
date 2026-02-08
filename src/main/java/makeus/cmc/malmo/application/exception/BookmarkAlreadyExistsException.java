package makeus.cmc.malmo.application.exception;

public class BookmarkAlreadyExistsException extends RuntimeException {

    public BookmarkAlreadyExistsException() {
        super("이미 북마크된 메시지입니다.");
    }

    public BookmarkAlreadyExistsException(String message) {
        super(message);
    }
}
