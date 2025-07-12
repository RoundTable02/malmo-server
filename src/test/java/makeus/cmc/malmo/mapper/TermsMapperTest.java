package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsTypeJpa;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.TermsMapper;
import makeus.cmc.malmo.domain.model.terms.Terms;
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
    @DisplayName("Entity를 Domain으로 변환할 때")
    class ToDomainTest {

        @Test
        @DisplayName("모든 필드가 있는 TermsEntity를 Terms로 변환한다")
        void givenCompleteEntity_whenToDomain_thenReturnsCompleteTerms() {
            // given
            TermsEntity entity = createCompleteEntity();

            // when
            Terms result = termsMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("개인정보 처리방침");
            assertThat(result.getContent()).isEqualTo("개인정보 처리방침 내용입니다.");
            assertThat(result.getVersion()).isEqualTo(1.0f);
            assertThat(result.isRequired()).isTrue();
            assertThat(result.getTermsType()).isEqualTo(TermsType.PRIVACY_POLICY);
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getModifiedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("null TermsType을 가진 Entity를 변환한다")
        void givenEntityWithNullTermsType_whenToDomain_thenReturnsTermsWithNullType() {
            // given
            TermsEntity entity = createEntityWithNullType();

            // when
            Terms result = termsMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTermsType()).isNull();
        }

        @Test
        @DisplayName("SERVICE TermsType을 가진 Entity를 변환한다")
        void givenServiceTermsType_whenToDomain_thenReturnsServiceType() {
            // given
            TermsEntity entity = createEntityWithType(TermsTypeJpa.SERVICE_USAGE);

            // when
            Terms result = termsMapper.toDomain(entity);

            // then
            assertThat(result.getTermsType()).isEqualTo(TermsType.SERVICE_USAGE);
        }

        @Test
        @DisplayName("MARKETING TermsType을 가진 Entity를 변환한다")
        void givenMarketingTermsType_whenToDomain_thenReturnsMarketingType() {
            // given
            TermsEntity entity = createEntityWithType(TermsTypeJpa.MARKETING);

            // when
            Terms result = termsMapper.toDomain(entity);

            // then
            assertThat(result.getTermsType()).isEqualTo(TermsType.MARKETING);
        }
    }

    @Nested
    @DisplayName("Domain을 Entity로 변환할 때")
    class ToEntityTest {

        @Test
        @DisplayName("모든 필드가 있는 Terms를 TermsEntity로 변환한다")
        void givenCompleteTerms_whenToEntity_thenReturnsCompleteEntity() {
            // given
            Terms domain = createCompleteTerms();

            // when
            TermsEntity result = termsMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("개인정보 처리방침");
            assertThat(result.getContent()).isEqualTo("개인정보 처리방침 내용입니다.");
            assertThat(result.getVersion()).isEqualTo(1.0f);
            assertThat(result.isRequired()).isTrue();
            assertThat(result.getTermsType()).isEqualTo(TermsTypeJpa.PRIVACY_POLICY);
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getModifiedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("null TermsType을 가진 Terms를 변환한다")
        void givenTermsWithNullType_whenToEntity_thenReturnsEntityWithNullType() {
            // given
            Terms domain = createTermsWithNullType();

            // when
            TermsEntity result = termsMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTermsType()).isNull();
        }

        @Test
        @DisplayName("SERVICE TermsType을 가진 Terms를 변환한다")
        void givenServiceTermsType_whenToEntity_thenReturnsServiceType() {
            // given
            Terms domain = createTermsWithType(TermsType.SERVICE_USAGE);

            // when
            TermsEntity result = termsMapper.toEntity(domain);

            // then
            assertThat(result.getTermsType()).isEqualTo(TermsTypeJpa.SERVICE_USAGE);
        }

        @Test
        @DisplayName("MARKETING TermsType을 가진 Terms를 변환한다")
        void givenMarketingTermsType_whenToEntity_thenReturnsMarketingType() {
            // given
            Terms domain = createTermsWithType(TermsType.MARKETING);

            // when
            TermsEntity result = termsMapper.toEntity(domain);

            // then
            assertThat(result.getTermsType()).isEqualTo(TermsTypeJpa.MARKETING);
        }
    }

    // Test data creation methods
    private TermsEntity createCompleteEntity() {
        return TermsEntity.builder()
                .id(1L)
                .title("개인정보 처리방침")
                .content("개인정보 처리방침 내용입니다.")
                .version(1.0f)
                .isRequired(true)
                .termsType(TermsTypeJpa.PRIVACY_POLICY)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

    private TermsEntity createEntityWithNullType() {
        return TermsEntity.builder()
                .id(1L)
                .title("약관")
                .content("내용")
                .version(1.0f)
                .isRequired(true)
                .termsType(null)
                .build();
    }

    private TermsEntity createEntityWithType(TermsTypeJpa type) {
        return TermsEntity.builder()
                .id(1L)
                .title("약관")
                .content("내용")
                .version(1.0f)
                .isRequired(true)
                .termsType(type)
                .build();
    }

    private Terms createCompleteTerms() {
        return Terms.builder()
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

    private Terms createTermsWithNullType() {
        return Terms.builder()
                .id(1L)
                .title("약관")
                .content("내용")
                .version(1.0f)
                .isRequired(true)
                .termsType(null)
                .build();
    }

    private Terms createTermsWithType(TermsType type) {
        return Terms.builder()
                .id(1L)
                .title("약관")
                .content("내용")
                .version(1.0f)
                .isRequired(true)
                .termsType(type)
                .build();
    }
}