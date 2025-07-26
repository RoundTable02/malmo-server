package makeus.cmc.malmo.application.service;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckCoupleMember;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.GetMemberUseCase;
import makeus.cmc.malmo.application.port.in.GetPartnerUseCase;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.LoadPartnerPort;
import makeus.cmc.malmo.domain.exception.MemberNotFoundException;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;
import makeus.cmc.malmo.domain.value.type.Provider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberInfoService implements GetMemberUseCase, GetPartnerUseCase {

    private final LoadMemberPort loadMemberPort;
    private final LoadPartnerPort loadPartnerPort;

    @Override
    @CheckValidMember
    public MemberResponseDto getMemberInfo(MemberInfoCommand command) {
        MemberInfoDto member = loadMemberPort.loadMemberDetailsById(MemberId.of(command.getUserId()))
                .orElseThrow(MemberNotFoundException::new);

        return MemberResponseDto.builder()
                .memberState(MemberState.valueOf(member.getMemberState()))
                .provider(member.getProvider())
                .startLoveDate(member.getStartLoveDate())
                .avoidanceRate(member.getAvoidanceRate())
                .anxietyRate(member.getAnxietyRate())
                .loveTypeCategory(member.getLoveTypeCategory())
                .totalChatRoomCount(member.getTotalChatRoomCount())
                .totalCoupleQuestionCount(
                        // 오늘의 질문은 항상 1개 이상 존재 (임시 질문 포함)
                        member.getTotalCoupleQuestionCount() == 0 ? 1 : member.getTotalCoupleQuestionCount()
                )
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

    @Data
    @Builder
    public static class MemberInfoDto {
        private String memberState;
        private Provider provider;
        private LocalDate startLoveDate;
        private LoveTypeCategory loveTypeCategory;
        private float avoidanceRate;
        private float anxietyRate;
        private String nickname;
        private String email;

        private int totalChatRoomCount;
        private int totalCoupleQuestionCount;
    }
}
