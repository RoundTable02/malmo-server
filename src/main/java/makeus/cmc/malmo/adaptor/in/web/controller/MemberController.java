package makeus.cmc.malmo.adaptor.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.in.web.docs.ApiCommonResponses;
import makeus.cmc.malmo.adaptor.in.web.docs.SwaggerResponses;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseListResponse;
import makeus.cmc.malmo.adaptor.in.web.dto.BaseResponse;
import makeus.cmc.malmo.application.port.in.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "멤버 관리 API", description = "Member 조회, 갱신 관련 API")
@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final GetMemberUseCase getMemberUseCase;
    private final GetPartnerUseCase getPartnerUseCase;
    private final GetInviteCodeUseCase getInviteCodeUseCase;
    private final UpdateMemberUseCase updateMemberUseCase;
    private final UpdateTermsAgreementUseCase updateTermsAgreementUseCase;
    private final UpdateMemberLoveTypeUseCase updateMemberLoveTypeUseCase;

    @Operation(
            summary = "멤버 정보 조회",
            description = "현재 로그인된 멤버 정보를 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "멤버 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.MemberInfoSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @GetMapping
    public BaseResponse<GetMemberUseCase.MemberResponseDto> getMemberInfo(
            @AuthenticationPrincipal User user
    ) {
        GetMemberUseCase.MemberInfoCommand command = GetMemberUseCase.MemberInfoCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .build();
        return BaseResponse.success(getMemberUseCase.getMemberInfo(command));
    }

    @Operation(
            summary = "커플 상대 정보 조회",
            description = "현재 로그인된 멤버의 파트너 정보를 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "파트너 멤버 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.PartnerMemberInfoSuccessResponse.class))
    )
    @ApiCommonResponses.OnlyCouple
    @ApiCommonResponses.RequireAuth
    @GetMapping("/partner")
    public BaseResponse<GetPartnerUseCase.PartnerMemberResponseDto> getPartnerMemberInfo(
            @AuthenticationPrincipal User user
    ) {
        GetPartnerUseCase.PartnerInfoCommand command = GetPartnerUseCase.PartnerInfoCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .build();
        return BaseResponse.success(getPartnerUseCase.getMemberInfo(command));
    }

    @Operation(
            summary = "사용자 정보 수정",
            description = "현재 로그인된 사용자의 정보를 수정합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "사용자 정보 수정 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.UpdateMemberSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @PatchMapping
    public BaseResponse<UpdateMemberUseCase.UpdateMemberResponseDto> updateMember(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateMemberRequestDto requestDto
    ) {
        UpdateMemberUseCase.UpdateMemberCommand command = UpdateMemberUseCase.UpdateMemberCommand.builder()
                .memberId(Long.valueOf(user.getUsername()))
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .build();
        return BaseResponse.success(updateMemberUseCase.updateMember(command));
    }

    @Operation(
            summary = "사용자 약관 동의 수정",
            description = "현재 로그인된 사용자의 약관 동의 정보를 수정합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "사용자 약관 동의 수정 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.UpdateMemberTermsSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @PatchMapping("/terms")
    public BaseResponse<BaseListResponse<UpdateTermsAgreementUseCase.TermsDto>> updateMemberTerms(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateMemberTermsRequestDto requestDto
    ) {
        List<UpdateTermsAgreementUseCase.TermsDto> termsCommands = requestDto.getTerms().stream()
                .map(term -> UpdateTermsAgreementUseCase.TermsDto.builder()
                        .termsId(term.getTermsId())
                        .isAgreed(term.getIsAgreed())
                        .build())
                .toList();

        UpdateTermsAgreementUseCase.TermsAgreementCommand command = UpdateTermsAgreementUseCase.TermsAgreementCommand
                .builder()
                .memberId(Long.valueOf(user.getUsername()))
                .terms(termsCommands)
                .build();
        return BaseListResponse.success(updateTermsAgreementUseCase.updateTermsAgreement(command).getTerms());
    }

    @Operation(
            summary = "사용자 초대 코드 조회",
            description = "현재 로그인된 사용자의 초대 코드를 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "사용자 초대 코드 조회 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.GetInviteCodeSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @GetMapping("/invite-code")
    public BaseResponse<GetInviteCodeUseCase.InviteCodeResponseDto> getMemberInviteCode(
            @AuthenticationPrincipal User user
    ) {
        GetInviteCodeUseCase.InviteCodeCommand command = GetInviteCodeUseCase.InviteCodeCommand.builder()
                .userId(Long.valueOf(user.getUsername()))
                .build();
        return BaseResponse.success(getInviteCodeUseCase.getInviteCode(command));
    }

    @Operation(
            summary = "🚧 [개발 전] 사용자 탈퇴",
            description = "현재 로그인된 사용자의 탈퇴를 처리합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "사용자 탈퇴 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.DeleteMemberSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @DeleteMapping
    public BaseResponse<DeleteMemberResponseDto> deleteMember(
            @AuthenticationPrincipal User user
    ) {
        return BaseResponse.success(DeleteMemberResponseDto.builder().build());
    }

    @Operation(
            summary = " 애착 유형 검사 결과 등록",
            description = "애착 유형 검사의 결과를 등록합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(
            responseCode = "200",
            description = "애착 유형 등록 성공",
            content = @Content(schema = @Schema(implementation = SwaggerResponses.RegisterLoveTypeSuccessResponse.class))
    )
    @ApiCommonResponses.RequireAuth
    @PostMapping("/love-type")
    public BaseResponse<UpdateMemberLoveTypeUseCase.RegisterLoveTypeResponseDto> registerLoveType(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody RegisterLoveTypeRequestDto requestDto
    ) {
        List<UpdateMemberLoveTypeUseCase.LoveTypeTestResult> results = requestDto.getResults().stream()
                .map(result -> UpdateMemberLoveTypeUseCase.LoveTypeTestResult.builder()
                        .questionId(result.getQuestionId())
                        .score(result.getScore())
                        .build())
                .toList();

        UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand command =
                UpdateMemberLoveTypeUseCase.UpdateMemberLoveTypeCommand.builder()
                .memberId(Long.valueOf(user.getUsername()))
                .results(results)
                .build();
        return BaseResponse.success(updateMemberLoveTypeUseCase.updateMemberLoveType(command));
    }

    @Data
    @Builder
    public static class DeleteMemberResponseDto {
        private Long memberId;
    }

    @Data
    public static class UpdateMemberRequestDto {
        private String nickname;
        private String email;
    }

    @Data
    public static class UpdateMemberTermsRequestDto {
        private List<TermsDto> terms;
    }

    @Data
    public static class TermsDto {
        @NotNull(message = "약관 ID는 필수 입력값입니다.")
        private Long termsId;
        @NotNull(message = "약관 동의 여부는 필수 입력값입니다.")
        private Boolean isAgreed;
    }

    @Data
    public static class RegisterLoveTypeRequestDto {
        private List<LoveTypeTestResult> results;
    }

    @Data
    public static class LoveTypeTestResult {
        @NotNull(message = "질문 ID는 필수 입력값입니다.")
        private Long questionId;
        @NotNull(message = "점수는 필수 입력값입니다.")
        @Max(5) @Min(1)
        private Integer score;
    }

}
