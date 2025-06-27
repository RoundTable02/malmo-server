package makeus.cmc.malmo.adaptor.in.web.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NO_SUCH_MEMBER(HttpStatus.BAD_REQUEST, 40001, "레이싱이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
