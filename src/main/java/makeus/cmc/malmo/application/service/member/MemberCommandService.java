package makeus.cmc.malmo.application.service.member;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.helper.couple.CoupleCommandHelper;
import makeus.cmc.malmo.application.helper.couple.CoupleQueryHelper;
import makeus.cmc.malmo.application.helper.member.MemberCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberMemoryCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.helper.member.OauthTokenHelper;
import makeus.cmc.malmo.application.port.in.couple.CoupleUnlinkUseCase;
import makeus.cmc.malmo.application.port.in.member.DeleteMemberUseCase;
import makeus.cmc.malmo.application.port.in.member.UpdateMemberUseCase;
import makeus.cmc.malmo.application.port.in.member.UpdateStartLoveDateUseCase;
import makeus.cmc.malmo.application.port.out.SendSseEventPort;
import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.Provider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static makeus.cmc.malmo.application.port.out.SendSseEventPort.SseEventType.COUPLE_DISCONNECTED;

@Service
@RequiredArgsConstructor
public class MemberCommandService implements UpdateMemberUseCase, UpdateStartLoveDateUseCase, DeleteMemberUseCase {

    private final CoupleQueryHelper coupleQueryHelper;
    private final CoupleCommandHelper coupleCommandHelper;

    private final MemberQueryHelper memberQueryHelper;
    private final MemberCommandHelper memberCommandHelper;
    private final OauthTokenHelper oauthTokenHelper;
    private final MemberMemoryCommandHelper memberMemoryCommandHelper;
    private final SendSseEventPort sendSseEventPort;

    @Override
    @CheckValidMember
    @Transactional
    public UpdateMemberResponseDto updateMember(UpdateMemberCommand command) {
        Member member = memberQueryHelper.getMemberByIdOrThrow(MemberId.of(command.getMemberId()));

        member.updateMemberProfile(command.getNickname());

        Member savedMember = memberCommandHelper.saveMember(member);

        return UpdateMemberResponseDto.builder()
                .nickname(savedMember.getNickname())
                .build();
    }

    @Override
    @CheckValidMember
    @Transactional
    public UpdateStartLoveDateResponse updateStartLoveDate(UpdateStartLoveDateCommand command) {
        Member member = memberQueryHelper.getMemberByIdOrThrow(MemberId.of(command.getMemberId()));
        LocalDate startLoveDate = command.getStartLoveDate();

        member.updateStartLoveDate(startLoveDate);
        Member savedMember = memberCommandHelper.saveMember(member);

        coupleQueryHelper.getCoupleById(member.getCoupleId())
                .ifPresent(couple -> {
                    couple.updateStartLoveDate(startLoveDate);
                    coupleCommandHelper.saveCouple(couple);
                });

        return UpdateStartLoveDateResponse.builder()
                .startLoveDate(savedMember.getStartLoveDate())
                .build();
    }

    @Override
    @CheckValidMember
    @Transactional
    public void deleteMember(DeleteMemberCommand command) {
        Member member = memberQueryHelper.getMemberByIdOrThrow(MemberId.of(command.getMemberId()));
        // OAuth를 통한 계정 연결 해제
        if (member.getProvider() == Provider.KAKAO) {
            oauthTokenHelper.unlinkKakao(member.getProviderId());
        } else if (member.getProvider() == Provider.APPLE) {
            oauthTokenHelper.unlinkApple(member.getOauthToken());
        }

        // 멤버 soft delete
        member.delete();
        memberCommandHelper.saveMember(member);

        // 멤버 채팅방, 커플 soft delete
        coupleQueryHelper.getCoupleByMemberId(MemberId.of(command.getMemberId()))
                        .ifPresent(couple -> coupleUnlink(member, couple));
    }

    public void coupleUnlink(Member member, Couple couple) {
        MemberId partnerId = couple.getOtherMemberId(MemberId.of(member.getId()));

        // 사용자의 커플 메모리 제거
        memberMemoryCommandHelper.deleteCoupleMemberMemory(member.getCoupleId(), MemberId.of(member.getId()));

        // 커플 해제 처리
        couple.unlink(MemberId.of(member.getId()),
                member.getNickname(),
                member.getLoveTypeCategory(),
                member.getAnxietyRate(),
                member.getAvoidanceRate());
        coupleCommandHelper.saveCouple(couple);

        member.unlinkCouple();
        memberCommandHelper.saveMember(member);

        // 상대방에게 커플 해지됨 알림
        sendSseEventPort.sendToMember(partnerId,
                new SendSseEventPort.NotificationEvent(COUPLE_DISCONNECTED, couple.getId())
        );
    }
}
