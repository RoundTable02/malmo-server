package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckCoupleMember;
import makeus.cmc.malmo.application.port.in.GetMemberUseCase;
import makeus.cmc.malmo.application.port.in.GetPartnerUseCase;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.LoadPartnerPort;
import makeus.cmc.malmo.domain.exception.MemberNotFoundException;
import makeus.cmc.malmo.domain.value.state.MemberState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberInfoService implements GetMemberUseCase, GetPartnerUseCase {

    private final LoadMemberPort loadMemberPort;
    private final LoadPartnerPort loadPartnerPort;

    @Override
    public MemberResponseDto getMemberInfo(MemberInfoCommand command) {
        LoadMemberPort.MemberResponseRepositoryDto member = loadMemberPort.loadMemberDetailsById(command.getUserId())
                .orElseThrow(MemberNotFoundException::new);

        return MemberResponseDto.builder()
                .memberState(MemberState.valueOf(member.getMemberState()))
                .startLoveDate(member.getStartLoveDate())
                .avoidanceRate(member.getAvoidanceRate())
                .anxietyRate(member.getAnxietyRate())
                .loveTypeCategory(member.getLoveTypeCategory())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }

    @Override
    @CheckCoupleMember
    public PartnerMemberResponseDto getPartnerInfo(PartnerInfoCommand command) {
        LoadPartnerPort.PartnerMemberRepositoryDto partner = loadPartnerPort.loadPartnerByMemberId(command.getUserId())
                .orElseThrow(MemberNotFoundException::new);

        return PartnerMemberResponseDto.builder()
                .memberState(MemberState.valueOf(partner.getMemberState()))
                .loveTypeCategory(partner.getLoveTypeCategory())
                .avoidanceRate(partner.getAvoidanceRate())
                .anxietyRate(partner.getAnxietyRate())
                .nickname(partner.getNickname())
                .build();
    }
}
