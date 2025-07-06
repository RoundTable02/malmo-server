package makeus.cmc.malmo.adaptor.in.web.docs;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 공통 API 응답 어노테이션들
 */
public class ApiCommonResponses {

    /**
     * 인증이 필요한 API의 공통 응답
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            )
    })
    public @interface RequireAuth {
    }

    /**
     * 관리자 권한이 필요한 API의 공통 응답
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한이 없는 사용자 (관리자 권한 필요)",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            )
    })
    public @interface RequireAdmin {
    }

    /**
     * 토큰 관련 API의 공통 응답
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 토큰",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",  
                    description = "존재하지 않는 사용자",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            )
    })
    public @interface TokenRelated {
    }

    /**
     * 로그인 API 전용 응답
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "401",
                    description = "ID Token 검증 실패",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "사용자 정보 조회 중 오류 발생",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            )
    })
    public @interface Login {
    }

    /**
     * 회원가입 API 전용 응답
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "존재하지 않는 약관",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "커플 코드 생성에 실패했습니다",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            )
    })
    public @interface SignUp {
    }

    /**
     * 커플 전용 API 응답
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "403",
                    description = "커플 등록 전인 사용자입니다. 커플 등록 후 이용해주세요.",
                    content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))
            )
    })
    public @interface OnlyCouple {
    }
}