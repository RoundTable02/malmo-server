package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.application.port.in.SignInUseCase;
import makeus.cmc.malmo.application.port.out.GenerateTokenPort;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.application.port.out.ValidateOidcTokenPort;
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

    @Override
    @Transactional
    public TokenResponse signInKakao(SignInKakaoCommand command) {
        // 1. OIDC ID 토큰 검증
        String providerId = validateOidcTokenPort.validateKakao(command.getIdToken());

        // 2. ID 토큰에서 provider와 providerId 추출
        Member member = loadMemberPort.loadMember(Provider.KAKAO, providerId)
                // 3. 없으면 새로 생성 (자동 회원가입)
                .orElseGet(() -> {
                    Member newMember = Member.createMember(
                            Provider.KAKAO,
                            providerId,
                            MemberRole.MEMBER,
                            MemberState.ALIVE,
                            null
                    );
                    return saveMemberPort.saveMember(newMember);
                });

        // 4. JWT 토큰 발급
        TokenInfo tokenInfo = generateTokenPort.generateToken(member.getId(), member.getMemberRole());
        return TokenResponse.builder()
                .grantType(tokenInfo.getGrantType())
                .accessToken(tokenInfo.getAccessToken())
                .refreshToken(tokenInfo.getRefreshToken())
                .build();
    }
}
