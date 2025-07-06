package makeus.cmc.malmo.adaptor.in.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String requestId;
    private int code;
    private boolean success;
    private String message;

    public static ResponseEntity<ErrorResponse> of(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ErrorResponse(MDC.get("request_id"),
                                errorCode.getCode(),
                                false,
                                errorCode.getMessage()
                        )
                );
    }
}
