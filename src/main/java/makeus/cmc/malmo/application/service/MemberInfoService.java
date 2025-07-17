package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckCoupleMember;
import makeus.cmc.malmo.application.port.in.GetMemberUseCase;
import makeus.cmc.malmo.application.port.in.GetPartnerUseCase;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.LoadPartnerPort;
import makeus.cmc.malmo.domain.exception.MemberNotFoundException;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeData;
import makeus.cmc.malmo.domain.model.member.MemberState;
import makeus.cmc.malmo.domain.service.LoveTypeDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberInfoService implements GetMemberUseCase, GetPartnerUseCase {

    private final LoadMemberPort loadMemberPort;
    private final LoadPartnerPort loadPartnerPort;

    private final LoveTypeDataService loveTypeDataService;

    @Override
    public MemberResponseDto getMemberInfo(MemberInfoCommand command) {
        LoadMemberPort.MemberResponseRepositoryDto member = loadMemberPort.loadMemberDetailsById(command.getUserId())
                .orElseThrow(MemberNotFoundException::new);
        LoveTypeData loveTypeData = loveTypeDataService.getLoveTypeData(member.getLoveTypeCategory());

        MemberResponseDto dto = MemberResponseDto.builder()
                .memberState(MemberState.valueOf(member.getMemberState()))
                .startLoveDate(member.getStartLoveDate())
                .avoidanceRate(member.getAvoidanceRate())
                .anxietyRate(member.getAnxietyRate())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();

        if (loveTypeData != null) {
            dto.setLoveTypeTitle(loveTypeData.getName());
            dto.setLoveTypeImageUrl(loveTypeData.getImageUrl());
        }

        return dto;
    }

    @Override
    @CheckCoupleMember
    public PartnerMemberResponseDto getPartnerInfo(PartnerInfoCommand command) {
        LoadPartnerPort.PartnerMemberRepositoryDto partner = loadPartnerPort.loadPartnerByMemberId(command.getUserId())
                .orElseThrow(MemberNotFoundException::new);
        LoveTypeData loveTypeData = loveTypeDataService.getLoveTypeData(partner.getLoveTypeCategory());


        PartnerMemberResponseDto dto = PartnerMemberResponseDto.builder()
                .memberState(MemberState.valueOf(partner.getMemberState()))
                .avoidanceRate(partner.getAvoidanceRate())
                .anxietyRate(partner.getAnxietyRate())
                .nickname(partner.getNickname())
                .build();

        if (loveTypeData != null) {
            dto.setLoveTypeTitle(loveTypeData.getName());
            dto.setLoveTypeImageUrl(loveTypeData.getImageUrl());
        }

        return dto;
    }
}
