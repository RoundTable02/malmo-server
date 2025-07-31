package makeus.cmc.malmo.application.service.helper.member;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.application.exception.InvalidRefreshTokenException;
import makeus.cmc.malmo.application.port.out.GenerateTokenPort;
import makeus.cmc.malmo.application.port.out.ValidateTokenPort;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessTokenHelper {

    private final GenerateTokenPort generateTokenPort;
    private final ValidateTokenPort validateTokenPort;

    public TokenInfo generateToken(Long memberId, MemberRole role) {
        return generateTokenPort.generateToken(memberId, role);
    }

    public void validateRefreshToken(Member member, String refreshToken) {
        boolean isValid = validateTokenPort.validateToken(refreshToken);

        if (!isValid || !member.hasSameRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
    }

    public String getMemberIdFromRefreshToken(String refreshToken) {
        if (!validateTokenPort.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
        return validateTokenPort.getMemberIdFromToken(refreshToken);
    }
}
