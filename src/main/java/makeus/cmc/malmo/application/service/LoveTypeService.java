package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.UpdateMemberLoveTypeUseCase;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.service.LoveTypeDataService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoveTypeService implements UpdateMemberLoveTypeUseCase {

    private final LoveTypeDataService loveTypeDataService;
    private final MemberDomainService memberDomainService;
    private final SaveMemberPort saveMemberPort;

    @Override
    @Transactional
    @CheckValidMember
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
