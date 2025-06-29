package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.exception.InvalidRefreshTokenException;
import makeus.cmc.malmo.application.port.in.RefreshTokenUseCase;
import makeus.cmc.malmo.application.port.out.*;
import makeus.cmc.malmo.domain.model.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private final LoadMemberPort loadMemberPort;
    private final SaveMemberPort saveMemberPort;
    private final GenerateTokenPort generateTokenPort;
    private final ValidateTokenPort validateTokenPort;

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenCommand command) {
        // 1. Refresh 토큰 유효성 검증
        String refreshToken = command.getRefreshToken();
        if (!validateTokenPort.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        // 2. Refresh 토큰 일치 확인
        Member member = loadMemberPort.loadMemberById(command.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        if (!Objects.equals(member.getRefreshToken(), refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        // 3. Refresh 토큰 갱신
        TokenInfo tokenInfo = generateTokenPort.generateToken(member.getId(), member.getMemberRole());
        member.refreshMemberToken(tokenInfo.getRefreshToken());
        saveMemberPort.saveMember(member);

        return RefreshTokenUseCase.TokenResponse.builder()
                .grantType(tokenInfo.getGrantType())
                .accessToken(tokenInfo.getAccessToken())
                .refreshToken(tokenInfo.getRefreshToken())
                .build();
    }
}
