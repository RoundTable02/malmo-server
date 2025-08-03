package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckCoupleMember;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.GetMemberUseCase;
import makeus.cmc.malmo.application.port.in.GetPartnerUseCase;
import makeus.cmc.malmo.application.service.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.MemberState;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberInfoService implements GetMemberUseCase, GetPartnerUseCase {

    private final MemberQueryHelper memberQueryHelper;

    @Override
    @CheckValidMember
    public MemberResponseDto getMemberInfo(MemberInfoCommand command) {
        MemberQueryHelper.MemberInfoDto member = memberQueryHelper.getMemberInfoOrThrow(MemberId.of(command.getUserId()));

        return MemberResponseDto.builder()
                .memberState(MemberState.valueOf(member.getMemberState()))
                .provider(member.getProvider())
                .startLoveDate(member.getStartLoveDate())
                .avoidanceRate(member.getAvoidanceRate())
                .anxietyRate(member.getAnxietyRate())
                .loveTypeCategory(member.getLoveTypeCategory())
                .totalChatRoomCount(member.getTotalChatRoomCount())
                .totalCoupleQuestionCount(member.getTotalCoupleQuestionCount())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }

    @Override
    @CheckCoupleMember
    public PartnerMemberResponseDto getPartnerInfo(PartnerInfoCommand command) {
        MemberQueryHelper.PartnerMemberDto partner = memberQueryHelper.getPartnerInfoOrThrow(MemberId.of(command.getUserId()));

        return PartnerMemberResponseDto.builder()
                .memberState(MemberState.valueOf(partner.getMemberState()))
                .loveTypeCategory(partner.getLoveTypeCategory())
                .avoidanceRate(partner.getAvoidanceRate())
                .anxietyRate(partner.getAnxietyRate())
                .nickname(partner.getNickname())
                .build();
    }
}
