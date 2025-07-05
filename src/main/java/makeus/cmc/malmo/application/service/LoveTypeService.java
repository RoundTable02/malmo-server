package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.LoveTypeNotFoundException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.LoveTypeQuestionNotFoundException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.in.GetLoveTypeUseCase;
import makeus.cmc.malmo.application.port.in.UpdateMemberLoveTypeUseCase;
import makeus.cmc.malmo.application.port.out.LoadLoveTypePort;
import makeus.cmc.malmo.application.port.out.LoadLoveTypeQuestionsPort;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeQuestion;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.LoveTypeId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoveTypeService implements GetLoveTypeUseCase, UpdateMemberLoveTypeUseCase {

    private final LoadLoveTypePort loadLoveTypePort;
    private final LoadLoveTypeQuestionsPort loadLoveTypeQuestionsPort;
    private final LoadMemberPort loadMemberPort;
    private final SaveMemberPort saveMemberPort;

    @Override
    public GetLoveTypeResponseDto getLoveType(GetLoveTypeCommand command) {
        LoveType loveType = loadLoveTypePort.findLoveTypeById(command.getLoveTypeId())
                .orElseThrow(LoveTypeNotFoundException::new);

        return GetLoveTypeResponseDto.builder()
                .loveTypeId(loveType.getId())
                .title(loveType.getTitle())
                .summary(loveType.getSummary())
                .content(loveType.getContent())
                .imageUrl(loveType.getImageUrl())
                .build();
    }

    @Override
    public RegisterLoveTypeResponseDto updateMemberLoveType(UpdateMemberLoveTypeCommand command) {
        Member member = loadMemberPort.loadMemberById(command.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        List<LoveTypeQuestion> loveTypeQuestions = loadLoveTypeQuestionsPort.loadLoveTypeQuestions();
        Map<Long, LoveTypeQuestion> questionMap = loveTypeQuestions.stream()
                .collect(Collectors.toMap(LoveTypeQuestion::getId, q -> q));
        float anxietyScore = 0.0f;
        float avoidanceScore = 0.0f;
        List<LoveTypeTestResult> results = command.getResults();

        for (LoveTypeTestResult result : results) {
            LoveTypeQuestion question = questionMap.get(result.getQuestionId());
            if (question == null) {
                throw new LoveTypeQuestionNotFoundException();
            }
            if (question.isAnxietyType()) {
                anxietyScore += question.getScore(result.getScore());
            } else {
                avoidanceScore += question.getScore(result.getScore());
            }
        }

        LoveTypeCategory loveTypeCategory = LoveType.findLoveTypeCategory(avoidanceScore, anxietyScore);
        LoveType loveType = loadLoveTypePort.findLoveTypeByLoveTypeCategory(loveTypeCategory)
                .orElseThrow(LoveTypeNotFoundException::new);

        member.updateLoveTypeId(LoveTypeId.of(loveType.getId()), avoidanceScore, anxietyScore);
        saveMemberPort.saveMember(member);

        return RegisterLoveTypeResponseDto.builder()
                .loveTypeId(loveType.getId())
                .title(loveType.getTitle())
                .summary(loveType.getSummary())
                .content(loveType.getContent())
                .imageUrl(loveType.getImageUrl())
                .build();
    }
}
