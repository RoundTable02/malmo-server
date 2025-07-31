package makeus.cmc.malmo.application.service.helper.member;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.application.port.out.AppleIdTokenPort;
import makeus.cmc.malmo.application.port.out.GenerateTokenPort;
import makeus.cmc.malmo.application.port.out.kakaoIdTokenPort;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessTokenHelper {

    private final GenerateTokenPort generateTokenPort;

    public TokenInfo generateToken(Long memberId, MemberRole role) {
        return generateTokenPort.generateToken(memberId, role);
    }
}
