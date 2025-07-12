package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.UpdateMemberUseCase;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberCommandService implements UpdateMemberUseCase {

    private final MemberDomainService memberDomainService;
    private final SaveMemberPort saveMemberPort;

    @Override
    @Transactional
    public UpdateMemberResponseDto updateMember(UpdateMemberCommand command) {
        Member member = memberDomainService.getMemberById(MemberId.of(command.getMemberId()));

        member.updateMemberProfile(command.getNickname(), command.getEmail());

        Member savedMember = saveMemberPort.saveMember(member);

        return UpdateMemberResponseDto.builder()
                .nickname(savedMember.getNickname())
                .email(savedMember.getEmail())
                .build();
    }
}
