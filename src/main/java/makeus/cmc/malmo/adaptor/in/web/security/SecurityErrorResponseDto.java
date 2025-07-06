package makeus.cmc.malmo.adaptor.in.web.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import makeus.cmc.malmo.adaptor.in.exception.ErrorCode;
import org.slf4j.MDC;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecurityErrorResponseDto {
    private String requestId;
    private int code;
    private boolean success;
    private String message;

    public static SecurityErrorResponseDto from(ErrorCode errorCode) {
        return new SecurityErrorResponseDto(MDC.get("request_id"), errorCode.getCode(), false, errorCode.getMessage());
    }
}
