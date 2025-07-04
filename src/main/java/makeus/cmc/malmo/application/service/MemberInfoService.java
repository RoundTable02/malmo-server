package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.in.GetInviteCodeUseCase;
import makeus.cmc.malmo.application.port.in.GetMemberUseCase;
import makeus.cmc.malmo.application.port.in.GetPartnerUseCase;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.LoadPartnerPort;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.member.MemberState;
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
        Member member = loadMemberPort.loadMemberById(command.getUserId())
                .orElseThrow(MemberNotFoundException::new);

        return MemberResponseDto.builder()
                .memberState(member.getMemberState())
                .loveTypeTitle(member.getLoveType() == null ? null : member.getLoveType().getTitle())
                .avoidanceRate(member.getAvoidanceRate())
                .anxietyRate(member.getAnxietyRate())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }

    @Override
    public PartnerMemberResponseDto getMemberInfo(PartnerInfoCommand command) {
        LoadPartnerPort.PartnerMemberRepositoryDto partner = loadPartnerPort.loadPartnerByMemberId(command.getUserId())
                .orElseThrow(MemberNotFoundException::new);

        return PartnerMemberResponseDto.builder()
                .loveStartDate(partner.getLoveStartDate())
                .memberState(MemberState.valueOf(partner.getMemberState()))
                .loveTypeTitle(partner.getLoveTypeTitle())
                .avoidanceRate(partner.getAvoidanceRate())
                .anxietyRate(partner.getAnxietyRate())
                .nickname(partner.getNickname())
                .build();
    }
}
