package makeus.cmc.malmo.application.service.member;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.application.port.in.member.LogOutUseCase;
import makeus.cmc.malmo.application.port.in.member.SignInUseCase;
import makeus.cmc.malmo.application.helper.member.AccessTokenHelper;
import makeus.cmc.malmo.application.helper.member.MemberCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.helper.member.OauthTokenHelper;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.InviteCodeDomainService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.Provider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

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
        Supplier<String> emailSupplier = () -> oauthTokenHelper.fetchKakaoEmailOrThrow(command.getAccessToken());
        return processSignIn(Provider.KAKAO, providerId, emailSupplier);
    }

    @Override
    @Transactional
    public SignInResponse signInApple(SignInAppleCommand command) {
        String providerId = oauthTokenHelper.getAppleIdTokenOrThrow(command.getIdToken());
        Supplier<String> emailSupplier = () -> oauthTokenHelper.getAppleEmailOrThrow(command.getIdToken());
        return processSignIn(Provider.APPLE, providerId, emailSupplier);
    }

    private SignInResponse processSignIn(Provider provider, String providerId, Supplier<String> emailSupplier) {
        // 1. provider와 providerId로 회원 조회 또는 신규 생성
        Member member = memberQueryHelper.getMemberByProviderId(provider, providerId)
                .orElseGet(() -> createNewMember(provider, providerId, emailSupplier.get()));

        // 2. JWT 토큰 발급 및 저장
        TokenInfo tokenInfo = accessTokenHelper.generateToken(member.getId(), member.getMemberRole());
        member.refreshMemberToken(tokenInfo.getRefreshToken());
        memberCommandHelper.saveMember(member);

        // 3. 최종 응답 생성
        return buildSignInResponse(member, tokenInfo);
    }

    private Member createNewMember(Provider provider, String providerId, String email) {
        InviteCodeValue inviteCode = createInviteCode(); // 기존 초대코드 생성 로직 재사용
        Member newMember = memberDomainService.createMember(provider, providerId, email, inviteCode);
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
    public void logout(LogOutCommand command) {
        Member member = memberQueryHelper.getMemberByIdOrThrow(MemberId.of(command.getUserId()));
        // Refresh Token 만료 처리
        member.logOut();

        memberCommandHelper.saveMember(member);
    }
}