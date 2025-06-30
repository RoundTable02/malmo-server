package makeus.cmc.malmo.adaptor.in.web.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 40000, "잘못된 요청입니다."),
    NO_SUCH_MEMBER(HttpStatus.BAD_REQUEST, 40001, "레이싱이 존재하지 않습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 40100, "인증되지 않은 사용자입니다."),
    INVALID_ID_TOKEN(HttpStatus.UNAUTHORIZED, 40101, "ID Token 검증 과정에 오류가 발생하였습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 40102, "Refresh Token 검증 과정에 오류가 발생하였습니다."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, 40300, "접근 권한이 없습니다."),

    // 404 Not Found
    NOT_FOUND(HttpStatus.NOT_FOUND, 40400, "요청한 리소스를 찾을 수 없습니다."),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 40500, "허용되지 않은 HTTP 메소드입니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50000, "서버 내부 오류입니다."),
    COUPLE_CODE_GENERATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "커플 코드 생성에 실패했습니다."),

    // 502 Bad Gateway
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, 50200, "외부 API 호출 중 오류가 발생했습니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
