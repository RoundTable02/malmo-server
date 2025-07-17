package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.GetLoveTypeUseCase;
import makeus.cmc.malmo.application.port.in.UpdateMemberLoveTypeUseCase;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.LoveTypeId;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.LoveTypeDataService;
import makeus.cmc.malmo.domain.service.LoveTypeDomainService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoveTypeService implements GetLoveTypeUseCase, UpdateMemberLoveTypeUseCase {

    private final LoveTypeDataService loveTypeDataService;
    private final MemberDomainService memberDomainService;
    private final SaveMemberPort saveMemberPort;

    @Override
    public GetLoveTypeResponseDto getLoveType(GetLoveTypeCommand command) {
//        LoveType loveType = loveTypeDataService.getLoveTypeData(LoveTypeId.of(command.getLoveTypeId()));

        return GetLoveTypeResponseDto.builder()
                .build();
    }

    @Override
    @Transactional
    public void updateMemberLoveType(UpdateMemberLoveTypeCommand command) {
        Member member = memberDomainService.getMemberById(MemberId.of(command.getMemberId()));

        List<LoveTypeDataService.TestResultInput> testResultInputs = command.getResults().stream()
                .map(result -> new LoveTypeDataService.TestResultInput(result.getQuestionId(), result.getScore()))
                .collect(Collectors.toList());

        LoveTypeDataService.LoveTypeCalculationResult calculationResult = loveTypeDataService.findLoveTypeCategoryByTestResult(testResultInputs);

        LoveTypeCategory category = calculationResult.category();
        float avoidanceScore = calculationResult.avoidanceScore();
        float anxietyScore = calculationResult.anxietyScore();

        member.updateLoveTypeId(category, avoidanceScore, anxietyScore);
        saveMemberPort.saveMember(member);
    }
}
