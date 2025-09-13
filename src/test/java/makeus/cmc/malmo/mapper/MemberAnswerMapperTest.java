package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.MemberAnswerEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleQuestionEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberAnswerMapper;
import makeus.cmc.malmo.domain.model.question.MemberAnswer;
import makeus.cmc.malmo.domain.value.id.CoupleQuestionId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.MemberAnswerState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberAnswerMapper 테스트")
class MemberAnswerMapperTest {

    @InjectMocks
    private MemberAnswerMapper memberAnswerMapper;

    @Test
    @DisplayName("Entity를 Domain으로 변환한다")
    void toDomain() {
        // given
        LocalDateTime now = LocalDateTime.now();
        MemberAnswerEntity entity = MemberAnswerEntity.builder()
                .id(1L)
                .coupleQuestionEntityId(CoupleQuestionEntityId.of(100L))
                .memberEntityId(MemberEntityId.of(200L))
                .answer("answer")
                .memberAnswerState(MemberAnswerState.ALIVE)
                .createdAt(now)
                .modifiedAt(now)
                .deletedAt(null)
                .build();

        // when
        MemberAnswer domain = memberAnswerMapper.toDomain(entity);

        // then
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getCoupleQuestionId().getValue()).isEqualTo(entity.getCoupleQuestionEntityId().getValue());
        assertThat(domain.getMemberId().getValue()).isEqualTo(entity.getMemberEntityId().getValue());
        assertThat(domain.getAnswer()).isEqualTo(entity.getAnswer());
        assertThat(domain.getMemberAnswerState()).isEqualTo(entity.getMemberAnswerState());
        assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(domain.getModifiedAt()).isEqualTo(entity.getModifiedAt());
        assertThat(domain.getDeletedAt()).isEqualTo(entity.getDeletedAt());
    }

    @Test
    @DisplayName("Domain을 Entity로 변환한다")
    void toEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        MemberAnswer domain = MemberAnswer.from(
                1L,
                CoupleQuestionId.of(100L),
                MemberId.of(200L),
                "answer",
                MemberAnswerState.ALIVE,
                now,
                now,
                null
        );

        // when
        MemberAnswerEntity entity = memberAnswerMapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getCoupleQuestionEntityId().getValue()).isEqualTo(domain.getCoupleQuestionId().getValue());
        assertThat(entity.getMemberEntityId().getValue()).isEqualTo(domain.getMemberId().getValue());
        assertThat(entity.getAnswer()).isEqualTo(domain.getAnswer());
        assertThat(entity.getMemberAnswerState()).isEqualTo(domain.getMemberAnswerState());
        assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
        assertThat(entity.getModifiedAt()).isEqualTo(domain.getModifiedAt());
        assertThat(entity.getDeletedAt()).isEqualTo(domain.getDeletedAt());
    }
}