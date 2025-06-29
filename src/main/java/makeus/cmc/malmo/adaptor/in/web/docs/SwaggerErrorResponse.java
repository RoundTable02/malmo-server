package makeus.cmc.malmo.adaptor.in.web.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "기본 응답 형식")
public class SwaggerErrorResponse {

    @Schema(description = "요청 ID", example = "e762d840-9565-4612-b308-42d1a50dc0c2")
    private String requestId;

    @Schema(description = "성공 여부", example = "false")
    private boolean success;

    @Schema(description = "응답 메시지", example = "인증되지 않은 사용자입니다.")
    private String message;
}