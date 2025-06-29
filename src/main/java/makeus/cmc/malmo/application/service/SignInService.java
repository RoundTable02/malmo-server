package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.application.port.in.SignInUseCase;
import makeus.cmc.malmo.application.port.out.*;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.member.MemberRole;
import makeus.cmc.malmo.domain.model.member.MemberState;
import makeus.cmc.malmo.domain.model.member.Provider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignInService implements SignInUseCase {

    private final LoadMemberPort loadMemberPort;
    private final SaveMemberPort saveMemberPort;
    private final GenerateTokenPort generateTokenPort;
    private final ValidateOidcTokenPort validateOidcTokenPort;

    private final FetchEmailFromOAuthProviderPort fetchEmailFromOAuthProviderPort;

    @Override
    @Transactional
    public SignInResponse signInKakao(SignInKakaoCommand command) {
        // 1. OIDC ID 토큰 검증
        String providerId = validateOidcTokenPort.validateKakao(command.getIdToken());

        // 2. ID 토큰에서 provider와 providerId 추출
        Member member = loadMemberPort.loadMemberByProviderId(Provider.KAKAO, providerId)
                // 3. 없으면 새로 생성 (자동 회원가입)
                .orElseGet(() -> {
                    // 이메일 정보 가져오기
                    String email = fetchEmailFromOAuthProviderPort.fetchEmailFromKakaoIdToken(command.getAccessToken());

                    Member newMember = Member.createMember(
                            Provider.KAKAO,
                            providerId,
                            MemberRole.MEMBER,
                            MemberState.BEFORE_ONBOARDING,
                            email,
                            null
                    );
                    return newMember;
                });


        // 4. JWT 토큰 발급
        TokenInfo tokenInfo = generateTokenPort.generateToken(member.getId(), member.getMemberRole());

        // 5. 멤버 정보 갱신 (리프레시 토큰 저장)
        member.refreshMemberToken(tokenInfo.getRefreshToken());
        saveMemberPort.saveMember(member);

        return SignInResponse.builder()
                .memberState(member.getMemberState().name())
                .grantType(tokenInfo.getGrantType())
                .accessToken(tokenInfo.getAccessToken())
                .refreshToken(tokenInfo.getRefreshToken())
                .build();
    }
}
