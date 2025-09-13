package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.question.CoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.TempCoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.CoupleQuestionMapper;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.QuestionMapper;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.Question;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CoupleQuestionMapper 테스트")
class CoupleQuestionMapperTest {

    @InjectMocks
    private CoupleQuestionMapper coupleQuestionMapper;

    @Mock
    private QuestionMapper questionMapper;

    private Question questionDomain;
    private QuestionEntity questionEntity;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        questionDomain = Question.from(1L, "t", "c", 1, now, now, null);
        questionEntity = QuestionEntity.builder().id(1L).title("t").content("c").level(1).createdAt(now).modifiedAt(now).deletedAt(null).build();
    }

    @Nested
    @DisplayName("CoupleQuestion <-> CoupleQuestionEntity")
    class CoupleQuestionTest {
        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void toDomain() {
            // given
            LocalDateTime now = LocalDateTime.now();
            CoupleQuestionEntity entity = CoupleQuestionEntity.builder()
                    .id(1L)
                    .question(questionEntity)
                    .coupleEntityId(CoupleEntityId.of(100L))
                    .coupleQuestionState(CoupleQuestionState.COMPLETED)
                    .bothAnsweredAt(now)
                    .createdAt(now)
                    .modifiedAt(now)
                    .deletedAt(null)
                    .build();

            when(questionMapper.toDomain(any(QuestionEntity.class))).thenReturn(questionDomain);

            // when
            CoupleQuestion domain = coupleQuestionMapper.toDomain(entity);

            // then
            assertThat(domain.getId()).isEqualTo(entity.getId());
            assertThat(domain.getQuestion()).isEqualTo(questionDomain);
            assertThat(domain.getCoupleId().getValue()).isEqualTo(entity.getCoupleEntityId().getValue());
            assertThat(domain.getCoupleQuestionState()).isEqualTo(entity.getCoupleQuestionState());
            assertThat(domain.getBothAnsweredAt()).isEqualTo(entity.getBothAnsweredAt());
            assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.getModifiedAt()).isEqualTo(entity.getModifiedAt());
            assertThat(domain.getDeletedAt()).isEqualTo(entity.getDeletedAt());
        }

        @Test
        @DisplayName("Domain을 Entity로 변환한다")
        void toEntity() {
            // given
            LocalDateTime now = LocalDateTime.now();
            CoupleQuestion domain = CoupleQuestion.from(1L, questionDomain, CoupleId.of(100L), CoupleQuestionState.COMPLETED, now, now, now, null);

            when(questionMapper.toEntity(any(Question.class))).thenReturn(questionEntity);

            // when
            CoupleQuestionEntity entity = coupleQuestionMapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.getId());
            assertThat(entity.getQuestion()).isEqualTo(questionEntity);
            assertThat(entity.getCoupleEntityId().getValue()).isEqualTo(domain.getCoupleId().getValue());
            assertThat(entity.getCoupleQuestionState()).isEqualTo(domain.getCoupleQuestionState());
            assertThat(entity.getBothAnsweredAt()).isEqualTo(domain.getBothAnsweredAt());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
            assertThat(entity.getModifiedAt()).isEqualTo(domain.getModifiedAt());
            assertThat(entity.getDeletedAt()).isEqualTo(domain.getDeletedAt());
        }
    }

    @Nested
    @DisplayName("TempCoupleQuestion <-> TempCoupleQuestionEntity")
    class TempCoupleQuestionTest {
        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void toDomain() {
            // given
            LocalDateTime now = LocalDateTime.now();
            TempCoupleQuestionEntity entity = TempCoupleQuestionEntity.builder()
                    .id(1L)
                    .question(questionEntity)
                    .memberId(MemberEntityId.of(200L))
                    .answer("answer")
                    .coupleQuestionState(CoupleQuestionState.ALIVE)
                    .answeredAt(now)
                    .createdAt(now)
                    .modifiedAt(now)
                    .deletedAt(null)
                    .build();

            when(questionMapper.toDomain(any(QuestionEntity.class))).thenReturn(questionDomain);


            // when
            TempCoupleQuestion domain = coupleQuestionMapper.toDomain(entity);

            // then
            assertThat(domain.getId()).isEqualTo(entity.getId());
            assertThat(domain.getQuestion()).isEqualTo(questionDomain);
            assertThat(domain.getMemberId().getValue()).isEqualTo(entity.getMemberId().getValue());
            assertThat(domain.getAnswer()).isEqualTo(entity.getAnswer());
            assertThat(domain.getCoupleQuestionState()).isEqualTo(entity.getCoupleQuestionState());
            assertThat(domain.getAnsweredAt()).isEqualTo(entity.getAnsweredAt());
            assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.getModifiedAt()).isEqualTo(entity.getModifiedAt());
            assertThat(domain.getDeletedAt()).isEqualTo(entity.getDeletedAt());
        }

        @Test
        @DisplayName("Domain을 Entity로 변환한다")
        void toEntity() {
            // given
            LocalDateTime now = LocalDateTime.now();
            TempCoupleQuestion domain = TempCoupleQuestion.from(1L, questionDomain, MemberId.of(200L), "answer", CoupleQuestionState.ALIVE, now, now, now, null);

            when(questionMapper.toEntity(any(Question.class))).thenReturn(questionEntity);

            // when
            TempCoupleQuestionEntity entity = coupleQuestionMapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.getId());
            assertThat(entity.getQuestion()).isEqualTo(questionEntity);
            assertThat(entity.getMemberId().getValue()).isEqualTo(domain.getMemberId().getValue());
            assertThat(entity.getAnswer()).isEqualTo(domain.getAnswer());
            assertThat(entity.getCoupleQuestionState()).isEqualTo(domain.getCoupleQuestionState());
            assertThat(entity.getAnsweredAt()).isEqualTo(domain.getAnsweredAt());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
            assertThat(entity.getModifiedAt()).isEqualTo(domain.getModifiedAt());
            assertThat(entity.getDeletedAt()).isEqualTo(domain.getDeletedAt());
        }
    }
}