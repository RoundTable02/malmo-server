package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.DeleteMemberUseCase;
import makeus.cmc.malmo.application.port.in.UpdateMemberUseCase;
import makeus.cmc.malmo.application.port.in.UpdateStartLoveDateUseCase;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.ChatRoomDomainService;
import makeus.cmc.malmo.domain.service.CoupleDomainService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService implements UpdateMemberUseCase, UpdateStartLoveDateUseCase, DeleteMemberUseCase {

    private final MemberDomainService memberDomainService;
    private final ChatRoomDomainService chatRoomDomainService;
    private final CoupleDomainService coupleDomainService;
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

    @Override
    public void deleteMember(DeleteMemberCommand command) {
        // 멤버 soft delete
        Member member = memberDomainService.getMemberById(MemberId.of(command.getMemberId()));
        memberDomainService.deleteMember(member);

        // 멤버 채팅방, 커플 soft delete
        chatRoomDomainService.deleteAllMemberChatRooms(MemberId.of(member.getId()));
        coupleDomainService.deleteCoupleByMemberId(MemberId.of(member.getId()));

        // TODO : 모든 커플 관련 엔티티 조회 로직을 커플의 STATE 조건을 걸도록 변경
        //  실제 Hard delete 시점에 하위 어그리거트 제거
    }
}
