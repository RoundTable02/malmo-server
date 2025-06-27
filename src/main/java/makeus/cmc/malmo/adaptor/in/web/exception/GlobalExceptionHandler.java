package makeus.cmc.malmo.adaptor.in.web.exception;

import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
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

}
