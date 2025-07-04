package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.in.UpdateMemberUseCase;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.domain.model.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberCommandService implements UpdateMemberUseCase {

    private final LoadMemberPort loadMemberPort;
    private final SaveMemberPort saveMemberPort;

    @Override
    @Transactional
    public UpdateMemberResponseDto updateMember(UpdateMemberCommand command) {
        Member member = loadMemberPort.loadMemberById(command.getMemberId())
                .orElseThrow(MemberNotFoundException::new);
        member.updateMemberProfile(command.getNickname(), command.getEmail());

        Member savedMember = saveMemberPort.saveMember(member);
        return UpdateMemberResponseDto.builder()
                .nickname(savedMember.getNickname())
                .email(savedMember.getEmail())
                .build();
    }
}
