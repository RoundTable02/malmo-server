package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.GetLoveTypeUseCase;
import makeus.cmc.malmo.application.port.in.UpdateMemberLoveTypeUseCase;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.LoveTypeDomainService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import makeus.cmc.malmo.domain.value.id.LoveTypeId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoveTypeService implements GetLoveTypeUseCase, UpdateMemberLoveTypeUseCase {

    private final LoveTypeDomainService loveTypeDomainService;
    private final MemberDomainService memberDomainService;
    private final SaveMemberPort saveMemberPort;

    @Override
    public GetLoveTypeResponseDto getLoveType(GetLoveTypeCommand command) {
        LoveType loveType = loveTypeDomainService.getLoveTypeById(LoveTypeId.of(command.getLoveTypeId()));

        return GetLoveTypeResponseDto.builder()
                .loveTypeId(loveType.getId())
                .title(loveType.getTitle())
                .summary(loveType.getSummary())
                .content(loveType.getContent())
                .imageUrl(loveType.getImageUrl())
                .build();
    }

    @Override
    @Transactional
    public RegisterLoveTypeResponseDto updateMemberLoveType(UpdateMemberLoveTypeCommand command) {
        Member member = memberDomainService.getMemberById(MemberId.of(command.getMemberId()));

        List<LoveTypeDomainService.TestResultInput> testResultInputs = command.getResults().stream()
                .map(result -> new LoveTypeDomainService.TestResultInput(result.getQuestionId(), result.getScore()))
                .collect(Collectors.toList());

        LoveTypeDomainService.LoveTypeCalculationResult calculationResult = loveTypeDomainService.calculateLoveType(testResultInputs);

        LoveType loveType = calculationResult.loveType();
        float avoidanceScore = calculationResult.avoidanceScore();
        float anxietyScore = calculationResult.anxietyScore();

        member.updateLoveTypeId(LoveTypeId.of(loveType.getId()), avoidanceScore, anxietyScore);
        saveMemberPort.saveMember(member);

        return RegisterLoveTypeResponseDto.builder()
                .avoidanceRate(member.getAvoidanceRate())
                .anxietyRate(member.getAnxietyRate())
                .loveTypeId(loveType.getId())
                .title(loveType.getTitle())
                .summary(loveType.getSummary())
                .content(loveType.getContent())
                .imageUrl(loveType.getImageUrl())
                .build();
    }
}
