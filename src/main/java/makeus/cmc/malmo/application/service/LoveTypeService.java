package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckCoupleMember;
import makeus.cmc.malmo.adaptor.in.exception.NotCoupleMemberException;
import makeus.cmc.malmo.application.port.in.GetMemberLoveTypeDetailsUseCase;
import makeus.cmc.malmo.application.port.in.UpdateMemberLoveTypeUseCase;
import makeus.cmc.malmo.application.port.out.LoadPartnerPort;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.LoveTypeDataService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import makeus.cmc.malmo.domain.service.MemberDomainValidationService;
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
    private final MemberDomainValidationService memberDomainValidationService;

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
        memberDomainValidationService.isMemberTested(MemberId.of(command.getMemberId()));

        Member member = memberDomainService.getMemberById(MemberId.of(command.getMemberId()));

        return LoveTypeDetailsDto.builder()
                .memberAnxietyScore(member.getAnxietyRate())
                .memberAvoidanceScore(member.getAvoidanceRate())
                .loveTypeCategory(member.getLoveTypeCategory())
                .build();
    }

    @Override
    @CheckCoupleMember
    public LoveTypeDetailsDto getPartnerLoveTypeInfo(MemberLoveTypeCommand command) {
        memberDomainValidationService.isMemberTested(MemberId.of(command.getMemberId()));

        LoadPartnerPort.PartnerLoveTypeRepositoryDto dto = loadPartnerPort.loadPartnerLoveTypeCategory(MemberId.of(command.getMemberId()))
                .orElseThrow(NotCoupleMemberException::new);

        LoveTypeCategory category = dto.getLoveTypeCategory();

        return LoveTypeDetailsDto.builder()
                .memberAnxietyScore(dto.getAnxietyRate())
                .memberAvoidanceScore(dto.getAvoidanceRate())
                .loveTypeCategory(dto.getLoveTypeCategory())
                .build();
    }
}
