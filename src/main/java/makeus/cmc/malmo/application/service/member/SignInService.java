package makeus.cmc.malmo.application.service.member;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.application.helper.member.AccessTokenHelper;
import makeus.cmc.malmo.application.helper.member.MemberCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.helper.member.OauthTokenHelper;
import makeus.cmc.malmo.application.port.in.member.LogOutUseCase;
import makeus.cmc.malmo.application.port.in.member.SignInUseCase;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.InviteCodeDomainService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.Provider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignInService implements SignInUseCase, LogOutUseCase {

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
        String providerId = oauthTokenHelper.getKakaoIdTokenOrThrow(command.getIdToken());
        Provider provider = Provider.KAKAO;

        Member member = memberQueryHelper.getMemberByProviderId(provider, providerId)
                .orElseGet(() -> {
                    // 카카오 Access Token을 이용해 OAuth로 이메일을 가져옴
                    String email = oauthTokenHelper.fetchKakaoEmailOrThrow(command.getAccessToken());
                    return createNewMember(provider, providerId, email, null);
                });

        // 어플리케이션 토큰 생성
        TokenInfo tokenInfo = accessTokenHelper.generateToken(member.getId(), member.getMemberRole());
        member.refreshMemberToken(tokenInfo.getRefreshToken());
        memberCommandHelper.saveMember(member);

        // 3. 최종 응답 생성
        return buildSignInResponse(member, tokenInfo);
    }

    @Override
    @Transactional
    public SignInResponse signInApple(SignInAppleCommand command) {
        String providerId = oauthTokenHelper.getAppleIdTokenOrThrow(command.getIdToken());
        Provider provider = Provider.APPLE;

        Member member = memberQueryHelper.getMemberByProviderId(provider, providerId)
                .orElseGet(() -> {
                    // idToken 복호화로 이메일을 가져옴
                    String email = oauthTokenHelper.getAppleEmailOrThrow(providerId);
                    // Apple OAuth 인증 코드로부터 Refresh Token을 가져옴
                    String oauthToken = oauthTokenHelper.fetchAppleRefreshTokenOrThrow(command.getAuthorizationCode());
                    return createNewMember(provider, providerId, email, oauthToken);
                });

        // 어플리케이션 토큰 생성
        TokenInfo tokenInfo = accessTokenHelper.generateToken(member.getId(), member.getMemberRole());
        member.refreshMemberToken(tokenInfo.getRefreshToken());
        memberCommandHelper.saveMember(member);

        return buildSignInResponse(member, tokenInfo);
    }

    private Member createNewMember(Provider provider, String providerId, String email, String oauthToken) {
        // 초대 코드 생성
        InviteCodeValue inviteCode = createInviteCode();
        Member newMember = memberDomainService.createMember(provider, providerId, email, inviteCode, oauthToken);
        return memberCommandHelper.saveMember(newMember);
    }

    private SignInResponse buildSignInResponse(Member member, TokenInfo tokenInfo) {
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

    @Override
    @CheckValidMember
    public void logout(LogOutCommand command) {
        Member member = memberQueryHelper.getMemberByIdOrThrow(MemberId.of(command.getUserId()));
        // Refresh Token 만료 처리
        member.logOut();

        memberCommandHelper.saveMember(member);
    }
}