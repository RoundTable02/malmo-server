package makeus.cmc.malmo.adaptor.in.web.exception;

import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.out.oidc.exception.OidcIdTokenException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.exception.InvalidRefreshTokenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler({InvalidRefreshTokenException.class})
    public ResponseEntity<ErrorResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        log.error("[GlobalExceptionHandler: handleInvalidRefreshTokenException 호출]", e);
        return ErrorResponse.of(ErrorCode.INVALID_REFRESH_TOKEN);
    }

}
