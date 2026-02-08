package makeus.cmc.malmo.application.exception;

public class BookmarkNotFoundException extends RuntimeException {

    public BookmarkNotFoundException() {
        super("북마크가 존재하지 않습니다.");
    }

    public BookmarkNotFoundException(String message) {
        super(message);
    }
}
