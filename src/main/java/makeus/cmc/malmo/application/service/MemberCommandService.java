package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.DeleteMemberUseCase;
import makeus.cmc.malmo.application.port.in.UpdateMemberUseCase;
import makeus.cmc.malmo.application.port.in.UpdateStartLoveDateUseCase;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.application.service.helper.couple.CoupleCommandHelper;
import makeus.cmc.malmo.application.service.helper.couple.CoupleQueryHelper;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberCommandService implements UpdateMemberUseCase, UpdateStartLoveDateUseCase, DeleteMemberUseCase {

    private final MemberDomainService memberDomainService;
    private final SaveMemberPort saveMemberPort;

    private final CoupleCommandHelper coupleCommandHelper;
    private final CoupleQueryHelper coupleQueryHelper;

    @Override
    @CheckValidMember
    @Transactional
    public UpdateMemberResponseDto updateMember(UpdateMemberCommand command) {
        Member member = memberDomainService.getMemberById(MemberId.of(command.getMemberId()));

        member.updateMemberProfile(command.getNickname());

        Member savedMember = saveMemberPort.saveMember(member);

        return UpdateMemberResponseDto.builder()
                .nickname(savedMember.getNickname())
                .build();
    }

    @Override
    @CheckValidMember
    @Transactional
    public UpdateStartLoveDateResponse updateStartLoveDate(UpdateStartLoveDateCommand command) {
        Member member = memberDomainService.getMemberById(MemberId.of(command.getMemberId()));
        Member updatedMember = memberDomainService.updateMemberStartLoveDate(member, command.getStartLoveDate());
        return UpdateStartLoveDateResponse.builder()
                .startLoveDate(updatedMember.getStartLoveDate())
                .build();
    }

    @Override
    @CheckValidMember
    @Transactional
    public void deleteMember(DeleteMemberCommand command) {
        // 멤버 soft delete
        Member member = memberDomainService.getMemberById(MemberId.of(command.getMemberId()));
        memberDomainService.deleteMember(member);

        // 멤버 채팅방, 커플 soft delete
        coupleQueryHelper.getCoupleByMemberId(MemberId.of(command.getMemberId()))
                        .ifPresent(couple -> {
                            couple.delete();
                            coupleCommandHelper.saveCouple(couple);
                        });
    }
}
