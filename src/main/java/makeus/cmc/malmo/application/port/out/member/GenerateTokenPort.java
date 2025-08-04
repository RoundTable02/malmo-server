package makeus.cmc.malmo.application.port.out.member;

import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.domain.value.type.MemberRole;

public interface GenerateTokenPort {
    TokenInfo generateToken(Long memberId, MemberRole role);
}
