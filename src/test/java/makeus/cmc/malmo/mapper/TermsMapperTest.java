package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsDetailsEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.TermsEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.TermsMapper;
import makeus.cmc.malmo.domain.model.terms.Terms;
import makeus.cmc.malmo.domain.model.terms.TermsDetails;
import makeus.cmc.malmo.domain.value.id.TermsId;
import makeus.cmc.malmo.domain.value.state.TermsDetailsType;
import makeus.cmc.malmo.domain.value.type.TermsType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("TermsMapper 테스트")
class TermsMapperTest {

    @InjectMocks
    private TermsMapper termsMapper;

    @Nested
    @DisplayName("Terms 매핑")
    class TermsMappingTest {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void toDomain() {
            // given
            TermsEntity entity = createCompleteTermsEntity();

            // when
            Terms domain = termsMapper.toDomain(entity);

            // then
            assertThat(domain.getId()).isEqualTo(entity.getId());
            assertThat(domain.getTitle()).isEqualTo(entity.getTitle());
            assertThat(domain.getContent()).isEqualTo(entity.getContent());
            assertThat(domain.getVersion()).isEqualTo(entity.getVersion());
            assertThat(domain.isRequired()).isEqualTo(entity.isRequired());
            assertThat(domain.getTermsType()).isEqualTo(entity.getTermsType());
            assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.getModifiedAt()).isEqualTo(entity.getModifiedAt());
            assertThat(domain.getDeletedAt()).isEqualTo(entity.getDeletedAt());
        }

        @Test
        @DisplayName("Domain을 Entity로 변환한다")
        void toEntity() {
            // given
            Terms domain = createCompleteTerms();

            // when
            TermsEntity entity = termsMapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.getId());
            assertThat(entity.getTitle()).isEqualTo(domain.getTitle());
            assertThat(entity.getContent()).isEqualTo(domain.getContent());
            assertThat(entity.getVersion()).isEqualTo(domain.getVersion());
            assertThat(entity.isRequired()).isEqualTo(domain.isRequired());
            assertThat(entity.getTermsType()).isEqualTo(domain.getTermsType());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
            assertThat(entity.getModifiedAt()).isEqualTo(domain.getModifiedAt());
            assertThat(entity.getDeletedAt()).isEqualTo(domain.getDeletedAt());
        }
    }

    @Nested
    @DisplayName("TermsDetails 매핑")
    class TermsDetailsMappingTest {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void toDomain() {
            // given
            TermsDetailsEntity entity = createCompleteTermsDetailsEntity();

            // when
            TermsDetails domain = termsMapper.toDomain(entity);

            // then
            assertThat(domain.getId()).isEqualTo(entity.getId());
            assertThat(domain.getTermsId().getValue()).isEqualTo(entity.getTermsEntityId().getValue());
            assertThat(domain.getTermsDetailsType()).isEqualTo(entity.getTermsDetailsType());
            assertThat(domain.getContent()).isEqualTo(entity.getContent());
        }

        @Test
        @DisplayName("Domain을 Entity로 변환한다")
        void toEntity() {
            // given
            TermsDetails domain = createCompleteTermsDetails();

            // when
            TermsDetailsEntity entity = termsMapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.getId());
            assertThat(entity.getTermsEntityId().getValue()).isEqualTo(domain.getTermsId().getValue());
            assertThat(entity.getTermsDetailsType()).isEqualTo(domain.getTermsDetailsType());
            assertThat(entity.getContent()).isEqualTo(domain.getContent());
        }
    }

    private TermsEntity createCompleteTermsEntity() {
        return TermsEntity.builder()
                .id(1L)
                .title("개인정보 처리방침")
                .content("개인정보 처리방침 내용입니다.")
                .version(1.0f)
                .isRequired(true)
                .termsType(TermsType.PRIVACY_POLICY)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

    private Terms createCompleteTerms() {
        return Terms.from(
                1L,
                "개인정보 처리방침",
                "개인정보 처리방침 내용입니다.",
                1.0f,
                true,
                TermsType.PRIVACY_POLICY,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private TermsDetailsEntity createCompleteTermsDetailsEntity() {
        return TermsDetailsEntity.builder()
                .id(1L)
                .termsEntityId(TermsEntityId.of(100L))
                .termsDetailsType(TermsDetailsType.CONTENT)
                .content("상세 내용")
                .build();
    }

    private TermsDetails createCompleteTermsDetails() {
        return TermsDetails.from(
                1L,
                TermsId.of(100L),
                TermsDetailsType.CONTENT,
                "상세 내용"
        );
    }
}
