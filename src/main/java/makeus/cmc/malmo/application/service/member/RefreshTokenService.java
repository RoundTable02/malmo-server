package makeus.cmc.malmo.application.service.member;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.application.port.in.member.RefreshTokenUseCase;
import makeus.cmc.malmo.application.helper.member.AccessTokenHelper;
import makeus.cmc.malmo.application.helper.member.MemberCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private final MemberQueryHelper memberQueryHelper;
    private final MemberCommandHelper memberCommandHelper;

    private final AccessTokenHelper accessTokenHelper;

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenCommand command) {
        String refreshToken = command.getRefreshToken();

        // Refresh 토큰 일치 및 토큰 유효성 검증
        String memberId = accessTokenHelper.getMemberIdFromRefreshToken(refreshToken);
        Member member = memberQueryHelper.getMemberByIdOrThrow(MemberId.of(Long.valueOf(memberId)));

        accessTokenHelper.validateRefreshToken(member, refreshToken);

        // Refresh 토큰 갱신
        TokenInfo tokenInfo = accessTokenHelper.generateToken(member.getId(), member.getMemberRole());
        member.refreshMemberToken(tokenInfo.getRefreshToken());

        memberCommandHelper.saveMember(member);

        return RefreshTokenUseCase.TokenResponse.builder()
                .grantType(tokenInfo.getGrantType())
                .accessToken(tokenInfo.getAccessToken())
                .refreshToken(tokenInfo.getRefreshToken())
                .build();
    }
}
