package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.exception.MemberNotTestedException;
import makeus.cmc.malmo.application.port.in.GetMemberLoveTypeDetailsUseCase;
import makeus.cmc.malmo.application.port.in.UpdateMemberLoveTypeUseCase;
import makeus.cmc.malmo.application.port.out.LoadPartnerPort;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeData;
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
public class LoveTypeService implements UpdateMemberLoveTypeUseCase, GetMemberLoveTypeDetailsUseCase {

    private final LoveTypeDataService loveTypeDataService;
    private final MemberDomainService memberDomainService;
    private final SaveMemberPort saveMemberPort;
    private final LoadPartnerPort loadPartnerPort;

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

    @Override
    public LoveTypeDetailsDto getMemberLoveTypeInfo(MemberLoveTypeCommand command) {
        Member member = memberDomainService.getMemberById(MemberId.of(command.getMemberId()));

        if (member.getLoveTypeCategory() == null) {
            throw new MemberNotTestedException();
        }
        LoveTypeCategory category = member.getLoveTypeCategory();
        LoveTypeData loveTypeData = loveTypeDataService.getLoveTypeData(category);

        return LoveTypeDetailsDto.builder()
                .memberAnxietyScore(member.getAnxietyRate())
                .memberAvoidanceScore(member.getAvoidanceRate())
                .name(loveTypeData.getName())
                .loveTypeName(loveTypeData.getLoveTypeName())
                .imageUrl(loveTypeData.getImageUrl())
                .summary(loveTypeData.getSummary())
                .description(loveTypeData.getDescription())
                .anxietyOver(
                        category.getAnxietyOver() == LoveTypeCategory.MIN_SCORE ? null : category.getAnxietyOver()
                )
                .anxietyUnder(
                        category.getAnxietyUnder() == LoveTypeCategory.MAX_SCORE ? null : category.getAnxietyUnder()
                )
                .avoidanceOver(
                        category.getAvoidanceOver() == LoveTypeCategory.MIN_SCORE ? null : category.getAvoidanceOver()
                )
                .avoidanceUnder(
                        category.getAvoidanceUnder() == LoveTypeCategory.MAX_SCORE ? null : category.getAvoidanceUnder()
                )
                .relationshipAttitudes(loveTypeData.getRelationshipAttitudes())
                .problemSolvingAttitudes(loveTypeData.getProblemSolvingAttitudes())
                .emotionalExpressions(loveTypeData.getEmotionalExpressions())
                .build();
    }

    @Override
    public LoveTypeDetailsDto getPartnerLoveTypeInfo(MemberLoveTypeCommand command) {
        LoadPartnerPort.PartnerLoveTypeRepositoryDto dto = loadPartnerPort.loadPartnerLoveTypeCategory(MemberId.of(command.getMemberId()))
                .orElseThrow(MemberNotTestedException::new);

        LoveTypeCategory category = dto.getLoveTypeCategory();

        LoveTypeData loveTypeData = loveTypeDataService.getLoveTypeData(category);

        return LoveTypeDetailsDto.builder()
                .memberAnxietyScore(dto.getAnxietyRate())
                .memberAvoidanceScore(dto.getAvoidanceRate())
                .name(loveTypeData.getName())
                .loveTypeName(loveTypeData.getLoveTypeName())
                .imageUrl(loveTypeData.getImageUrl())
                .summary(loveTypeData.getSummary())
                .description(loveTypeData.getDescription())
                .anxietyOver(
                        category.getAnxietyOver() == LoveTypeCategory.MIN_SCORE ? null : category.getAnxietyOver()
                )
                .anxietyUnder(
                        category.getAnxietyUnder() == LoveTypeCategory.MAX_SCORE ? null : category.getAnxietyUnder()
                )
                .avoidanceOver(
                        category.getAvoidanceOver() == LoveTypeCategory.MIN_SCORE ? null : category.getAvoidanceOver()
                )
                .avoidanceUnder(
                        category.getAvoidanceUnder() == LoveTypeCategory.MAX_SCORE ? null : category.getAvoidanceUnder()
                )
                .relationshipAttitudes(loveTypeData.getRelationshipAttitudes())
                .problemSolvingAttitudes(loveTypeData.getProblemSolvingAttitudes())
                .emotionalExpressions(loveTypeData.getEmotionalExpressions())
                .build();
    }
}
