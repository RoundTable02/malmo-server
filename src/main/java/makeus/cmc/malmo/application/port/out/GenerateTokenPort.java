package makeus.cmc.malmo.application.port.out;

import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.domain.model.member.MemberRole;

public interface GenerateTokenPort {
    TokenInfo generateToken(Long memberId, MemberRole role);
}
