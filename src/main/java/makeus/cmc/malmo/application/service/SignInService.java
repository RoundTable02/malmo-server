package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.application.port.in.SignInUseCase;
import makeus.cmc.malmo.application.port.out.*;
import makeus.cmc.malmo.application.service.helper.member.AccessTokenHelper;
import makeus.cmc.malmo.application.service.helper.member.OauthTokenHelper;
import makeus.cmc.malmo.application.service.helper.member.MemberCommandHelper;
import makeus.cmc.malmo.application.service.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.InviteCodeDomainService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.type.Provider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignInService implements SignInUseCase {

    private final MemberQueryHelper memberQueryHelper;
    private final MemberCommandHelper memberCommandHelper;
    private final MemberDomainService memberDomainService;

    private final InviteCodeDomainService inviteCodeDomainService;

    private final OauthTokenHelper oauthTokenHelper;
    private final AccessTokenHelper accessTokenHelper;

    private static final int MAX_RETRY = 10;

    @Override
    @Transactional
    public SignInResponse signInKakao(SignInKakaoCommand command) {
        // OIDC ID 토큰 검증
        String providerId = oauthTokenHelper.getKakaoIdTokenOrThrow(command.getIdToken());

        // ID 토큰에서 provider와 providerId 추출
        Member member = memberQueryHelper.getMemberByProviderId(Provider.KAKAO, providerId)
                // 없으면 새로 생성 (자동 회원가입)
                .orElseGet(() -> {
                    // 이메일 정보 가져오기
                    String email = oauthTokenHelper.fetchKakaoEmailOrThrow(command.getAccessToken());

                    // 초대 코드 생성
                    InviteCodeValue inviteCode = createInviteCode();

                    Member newMember = memberDomainService.createMember(Provider.KAKAO, providerId, email, inviteCode);
                    return memberCommandHelper.saveMember(newMember);
                });

        // 복귀 멤버 상태 복구
        if (member.isRevivable()) {
            member.revive();
        }

        // JWT 토큰 발급
        TokenInfo tokenInfo = accessTokenHelper.generateToken(member.getId(), member.getMemberRole());

        // 멤버 정보 갱신 (리프레시 토큰 저장)
        member.refreshMemberToken(tokenInfo.getRefreshToken());
        memberCommandHelper.saveMember(member);

        return SignInResponse.builder()
                .memberState(member.getMemberState().name())
                .grantType(tokenInfo.getGrantType())
                .accessToken(tokenInfo.getAccessToken())
                .refreshToken(tokenInfo.getRefreshToken())
                .build();
    }


    @Override
    @Transactional
    public SignInResponse signInApple(SignInAppleCommand command) {
        // OIDC ID 토큰 검증
        String providerId = oauthTokenHelper.getAppleIdTokenOrThrow(command.getIdToken());

        // ID 토큰에서 provider와 providerId 추출
        Member member = memberQueryHelper.getMemberByProviderId(Provider.APPLE, providerId)
                // 없으면 새로 생성 (자동 회원가입)
                .orElseGet(() -> {
                    // 이메일 정보 가져오기
                    String email = oauthTokenHelper.getAppleEmailOrThrow(command.getIdToken());

                    // 초대 코드 생성
                    InviteCodeValue inviteCode = createInviteCode();

                    Member newMember = memberDomainService.createMember(Provider.APPLE, providerId, email, inviteCode);
                    return memberCommandHelper.saveMember(newMember);
                });

        if (member.isRevivable()) {
            member.revive();
        }

        // JWT 토큰 발급
        TokenInfo tokenInfo = accessTokenHelper.generateToken(member.getId(), member.getMemberRole());

        // 멤버 정보 갱신 (리프레시 토큰 저장)
        member.refreshMemberToken(tokenInfo.getRefreshToken());
        memberCommandHelper.saveMember(member);

        return SignInResponse.builder()
                .memberState(member.getMemberState().name())
                .grantType(tokenInfo.getGrantType())
                .accessToken(tokenInfo.getAccessToken())
                .refreshToken(tokenInfo.getRefreshToken())
                .build();
    }

    private InviteCodeValue createInviteCode() {
        InviteCodeValue inviteCode = null;
        int retryCount = 0;
        while (retryCount < MAX_RETRY) {
            inviteCode = inviteCodeDomainService.generateInviteCode();
            if (memberQueryHelper.isInviteCodeValid(inviteCode)) {
                break;
            }
            retryCount++;
        }
        return inviteCode;
    }
}
