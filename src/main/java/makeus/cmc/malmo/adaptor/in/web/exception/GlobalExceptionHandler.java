package makeus.cmc.malmo.adaptor.in.web.exception;

import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.out.oidc.exception.OidcIdTokenException;
import makeus.cmc.malmo.adaptor.out.oidc.exception.RestApiException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.InviteCodeGenerateFailedException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.exception.InvalidRefreshTokenException;
import org.hibernate.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MemberNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException e) {
        log.error("[GlobalExceptionHandler: handleMemberNotFoundException 호출]", e);
        return ErrorResponse.of(ErrorCode.NO_SUCH_MEMBER);
    }

    @ExceptionHandler({OidcIdTokenException.class})
    public ResponseEntity<ErrorResponse> handleOidcIdTokenException(OidcIdTokenException e) {
        log.error("[GlobalExceptionHandler: handleOidcIdTokenException 호출]", e);
        return ErrorResponse.of(ErrorCode.INVALID_ID_TOKEN);
    }

    @ExceptionHandler({RestApiException.class})
    public ResponseEntity<ErrorResponse> handleRestApiException(RestApiException e) {
        log.error("[GlobalExceptionHandler: handleRestApiException 호출]", e);
        return ErrorResponse.of(ErrorCode.EXTERNAL_API_ERROR);
    }

    @ExceptionHandler({InvalidRefreshTokenException.class})
    public ResponseEntity<ErrorResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        log.error("[GlobalExceptionHandler: handleInvalidRefreshTokenException 호출]", e);
        return ErrorResponse.of(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    @ExceptionHandler({InviteCodeGenerateFailedException.class})
    public ResponseEntity<ErrorResponse> handleInviteCodeGenerateFailedException(InviteCodeGenerateFailedException e) {
        log.error("[GlobalExceptionHandler: handleInviteCodeGenerateFailedException 호출]", e);
        return ErrorResponse.of(ErrorCode.COUPLE_CODE_GENERATE_FAILED);
    }

    /**
     *  ---------- 공통 예외 처리 핸들러 ----------
      */
    @ExceptionHandler({NoHandlerFoundException.class, TypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception e) {
        log.error("[CommonExceptionHandler: handleBadRequestException 호출]", e);
        return ErrorResponse.of(ErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("[CommonExceptionHandler: handleHttpRequestMethodNotSupportedException 호출]", e);
        return ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({IllegalArgumentException.class, IOException.class})
    public ResponseEntity<ErrorResponse> handleInternalServerException(Exception e) {
        log.error("[CommonExceptionHandler: handleInternalServerException 호출]", e);
        return ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(Exception e) {
        log.error("[CommonExceptionHandler: handleRuntimeException 호출]", e);
        return ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
    }

}
