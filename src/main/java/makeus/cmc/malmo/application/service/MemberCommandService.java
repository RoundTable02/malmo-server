package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.UpdateMemberUseCase;
import makeus.cmc.malmo.application.port.in.UpdateStartLoveDateUseCase;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService implements UpdateMemberUseCase, UpdateStartLoveDateUseCase {

    private final MemberDomainService memberDomainService;
    private final SaveMemberPort saveMemberPort;

    @Override
    public UpdateMemberResponseDto updateMember(UpdateMemberCommand command) {
        Member member = memberDomainService.getMemberById(MemberId.of(command.getMemberId()));

        member.updateMemberProfile(command.getNickname());

        Member savedMember = saveMemberPort.saveMember(member);

        return UpdateMemberResponseDto.builder()
                .nickname(savedMember.getNickname())
                .build();
    }

    @Override
    public UpdateStartLoveDateResponse updateStartLoveDate(UpdateStartLoveDateCommand command) {
        Member member = memberDomainService.getMemberById(MemberId.of(command.getMemberId()));
        Member updatedMember = memberDomainService.updateMemberStartLoveDate(member, command.getStartLoveDate());
        return UpdateStartLoveDateResponse.builder()
                .startLoveDate(updatedMember.getStartLoveDate())
                .build();
    }
}
