package makeus.cmc.malmo.adaptor.in.web.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class SwaggerResponses {

    @Getter
    @Schema(description = "기본 응답 형식")
    public static class BaseSwaggerResponse<T> {
        @Schema(description = "요청 ID", example = "e762d840-9565-4612-b308-42d1a50dc0c2")
        private String requestId;

        @Schema(description = "성공 여부", example = "true")
        private boolean success;

        @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
        private String message;

        @Schema(description = "응답 데이터")
        private T data;
    }

    @Getter
    @Schema(description = "로그인 성공 응답")
    public static class LoginSuccessResponse extends BaseSwaggerResponse<LoginData> {
    }

    @Getter
    @Schema(description = "토큰 갱신 성공 응답")
    public static class RefreshTokenSuccessResponse extends BaseSwaggerResponse<TokenData> {
    }

    @Getter
    @Schema(description = "로그인 응답 데이터")
    public static class LoginData {
        @Schema(description = "토큰 타입", example = "Bearer")
        private String grantType;

        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String accessToken;

        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String refreshToken;

        @Schema(description = "멤버 상태", example = "ALIVE")
        private String memberState;
    }

    @Getter
    @Schema(description = "토큰 응답 데이터")
    public static class TokenData {
        @Schema(description = "토큰 타입", example = "Bearer")
        private String grantType;

        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String accessToken;

        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String refreshToken;
    }
}