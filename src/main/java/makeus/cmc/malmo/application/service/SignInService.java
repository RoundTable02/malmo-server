package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.application.port.in.SignInUseCase;
import makeus.cmc.malmo.application.port.out.GenerateTokenPort;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.member.MemberRole;
import makeus.cmc.malmo.domain.model.member.MemberState;
import makeus.cmc.malmo.domain.model.member.Provider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignInService implements SignInUseCase {

    private final LoadMemberPort loadMemberPort;
    private final SaveMemberPort saveMemberPort;
    private final GenerateTokenPort generateTokenPort;

    @Override
    @Transactional
    public TokenInfo signIn(SignInCommand command) {
        Provider provider = Provider.valueOf(command.getProvider().toUpperCase());

        // 1. Provider와 ProviderId로 회원 조회
        Member member = loadMemberPort.loadMember(provider, command.getProviderId())
                // 2. 없으면 새로 생성 (자동 회원가입)
                .orElseGet(() -> {
                    Member newMember = Member.createMember(
                            provider,
                            command.getProviderId(),
                            MemberRole.MEMBER,
                            MemberState.ALIVE,
                            null
                    );
                    return saveMemberPort.saveMember(newMember);
                });

        // 3. JWT 토큰 발급
        return generateTokenPort.generateToken(member.getId(), member.getMemberRole());
    }
}
