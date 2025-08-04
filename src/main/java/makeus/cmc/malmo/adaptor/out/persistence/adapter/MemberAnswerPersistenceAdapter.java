package makeus.cmc.malmo.adaptor.out.persistence.adapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.MemberAnswerEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberAnswerMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.question.MemberAnswerRepository;
import makeus.cmc.malmo.application.port.out.question.LoadMemberAnswerPort;
import makeus.cmc.malmo.application.port.out.question.SaveMemberAnswerPort;
import makeus.cmc.malmo.application.helper.question.CoupleQuestionQueryHelper;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class MemberAnswerPersistenceAdapter implements LoadMemberAnswerPort, SaveMemberAnswerPort {

    private final MemberAnswerRepository memberAnswerRepository;
    private final MemberAnswerMapper memberAnswerMapper;

    @Override
    public Optional<CoupleQuestionQueryHelper.MemberAnswersDto> getQuestionAnswers(MemberId memberId, CoupleQuestionId coupleQuestionId) {
        return memberAnswerRepository.findAnswersDtoByCoupleQuestionId(memberId.getValue(), coupleQuestionId.getValue())
                .map(AnswerRepositoryDto::toDto);
    }

    @Override
    public Optional<MemberAnswer> getMemberAnswer(CoupleQuestionId coupleQuestionId, MemberId memberId) {
        return memberAnswerRepository.findByCoupleQuestionIdAndCoupleMemberId(coupleQuestionId.getValue(), memberId.getValue())
                .map(memberAnswerMapper::toDomain);
    }

    @Override
    public boolean isMemberAnswered(CoupleQuestionId coupleQuestionId, MemberId memberId) {
        return memberAnswerRepository.existsByCoupleQuestionIdAndMemberId(coupleQuestionId.getValue(), memberId.getValue());
    }

    @Override
    public long countAnswers(CoupleQuestionId coupleQuestionId) {
        return memberAnswerRepository.countByCoupleQuestionIdAndMemberId(coupleQuestionId.getValue());
    }

    @Override
    public MemberAnswer saveMemberAnswer(MemberAnswer memberAnswer) {
        MemberAnswerEntity entity = memberAnswerMapper.toEntity(memberAnswer);
        MemberAnswerEntity savedEntity = memberAnswerRepository.save(entity);
        return memberAnswerMapper.toDomain(savedEntity);
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class AnswerRepositoryDto {
        private String title;
        private String content;
        private Integer level;
        private LocalDateTime createdAt;
        private String nickname;
        private String answer;
        private boolean updatable;
        private String partnerNickname;
        private String partnerAnswer;
        private boolean partnerUpdatable;

        public CoupleQuestionQueryHelper.MemberAnswersDto toDto() {
            return CoupleQuestionQueryHelper.MemberAnswersDto.builder()
                    .title(title)
                    .content(content)
                    .level(level)
                    .createdAt(createdAt)
                    .me(
                            CoupleQuestionQueryHelper.AnswerDto.builder()
                                    .nickname(nickname)
                                    .answer(answer)
                                    .updatable(updatable)
                                    .build()
                    )
                    .partner(
                            CoupleQuestionQueryHelper.AnswerDto.builder()
                                    .nickname(partnerNickname)
                                    .answer(partnerAnswer)
                                    .updatable(partnerUpdatable)
                                    .build()
                    )
                    .build();
        }
    }
}
