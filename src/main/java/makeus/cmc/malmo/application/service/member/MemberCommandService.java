package makeus.cmc.malmo.application.service.member;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.helper.couple.CoupleCommandHelper;
import makeus.cmc.malmo.application.helper.couple.CoupleQueryHelper;
import makeus.cmc.malmo.application.helper.member.MemberCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.helper.member.OauthTokenHelper;
import makeus.cmc.malmo.application.port.in.member.DeleteMemberUseCase;
import makeus.cmc.malmo.application.port.in.member.UpdateMemberUseCase;
import makeus.cmc.malmo.application.port.in.member.UpdateStartLoveDateUseCase;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.Provider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MemberCommandService implements UpdateMemberUseCase, UpdateStartLoveDateUseCase, DeleteMemberUseCase {

    private final CoupleQueryHelper coupleQueryHelper;
    private final CoupleCommandHelper coupleCommandHelper;

    private final MemberQueryHelper memberQueryHelper;
    private final MemberCommandHelper memberCommandHelper;
    private final OauthTokenHelper oauthTokenHelper;

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

        coupleQueryHelper.getCoupleByMemberId(MemberId.of(command.getMemberId()))
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
                        .ifPresent(couple -> {
                            couple.unlink(MemberId.of(member.getId()));
                            coupleCommandHelper.saveCouple(couple);
                        });
    }
}
