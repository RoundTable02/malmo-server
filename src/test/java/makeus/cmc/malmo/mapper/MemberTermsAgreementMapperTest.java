package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.MemberTermsAgreementEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.TermsEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberTermsAgreementMapper;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.id.TermsId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberTermsAgreementMapper 테스트")
class MemberTermsAgreementMapperTest {

    @InjectMocks
    private MemberTermsAgreementMapper memberTermsAgreementMapper;

    @Nested
    @DisplayName("Entity를 Domain으로 변환할 때")
    class ToDomainTest {

        @Test
        @DisplayName("모든 필드가 있는 MemberTermsAgreementEntity를 MemberTermsAgreement로 변환한다")
        void givenCompleteEntity_whenToDomain_thenReturnsCompleteMemberTermsAgreement() {
            // given
            MemberTermsAgreementEntity entity = createCompleteEntity();

            // when
            MemberTermsAgreement result = memberTermsAgreementMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getMemberId().getValue()).isEqualTo(100L);
            assertThat(result.getTermsId().getValue()).isEqualTo(200L);
            assertThat(result.isAgreed()).isTrue();
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getModifiedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("동의하지 않은 약관의 Entity를 변환한다")
        void givenDisagreedEntity_whenToDomain_thenReturnsDisagreedMemberTermsAgreement() {
            // given
            MemberTermsAgreementEntity entity = createDisagreedEntity();

            // when
            MemberTermsAgreement result = memberTermsAgreementMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isAgreed()).isFalse();
        }

        @Test
        @DisplayName("삭제된 약관 동의 Entity를 변환한다")
        void givenDeletedEntity_whenToDomain_thenReturnsDeletedMemberTermsAgreement() {
            // given
            LocalDateTime deletedAt = LocalDateTime.now();
            MemberTermsAgreementEntity entity = createDeletedEntity(deletedAt);

            // when
            MemberTermsAgreement result = memberTermsAgreementMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getDeletedAt()).isEqualTo(deletedAt);
        }
    }

    @Nested
    @DisplayName("Domain을 Entity로 변환할 때")
    class ToEntityTest {

        @Test
        @DisplayName("모든 필드가 있는 MemberTermsAgreement를 MemberTermsAgreementEntity로 변환한다")
        void givenCompleteMemberTermsAgreement_whenToEntity_thenReturnsCompleteEntity() {
            // given
            MemberTermsAgreement domain = createCompleteMemberTermsAgreement();

            // when
            MemberTermsAgreementEntity result = memberTermsAgreementMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getMemberEntityId().getValue()).isEqualTo(100L);
            assertThat(result.getTermsEntityId().getValue()).isEqualTo(200L);
            assertThat(result.isAgreed()).isTrue();
            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getModifiedAt()).isNotNull();
            assertThat(result.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("동의하지 않은 MemberTermsAgreement를 변환한다")
        void givenDisagreedMemberTermsAgreement_whenToEntity_thenReturnsDisagreedEntity() {
            // given
            MemberTermsAgreement domain = createDisagreedMemberTermsAgreement();

            // when
            MemberTermsAgreementEntity result = memberTermsAgreementMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isAgreed()).isFalse();
        }

        @Test
        @DisplayName("삭제된 MemberTermsAgreement를 변환한다")
        void givenDeletedMemberTermsAgreement_whenToEntity_thenReturnsDeletedEntity() {
            // given
            LocalDateTime deletedAt = LocalDateTime.now();
            MemberTermsAgreement domain = createDeletedMemberTermsAgreement(deletedAt);

            // when
            MemberTermsAgreementEntity result = memberTermsAgreementMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getDeletedAt()).isEqualTo(deletedAt);
        }

        @Test
        @DisplayName("null ID를 가진 MemberTermsAgreement를 변환한다")
        void givenMemberTermsAgreementWithNullId_whenToEntity_thenReturnsEntityWithNullId() {
            // given
            MemberTermsAgreement domain = createMemberTermsAgreementWithNullId();

            // when
            MemberTermsAgreementEntity result = memberTermsAgreementMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNull();
            assertThat(result.getMemberEntityId().getValue()).isEqualTo(100L);
            assertThat(result.getTermsEntityId().getValue()).isEqualTo(200L);
        }
    }

    // Test data creation methods
    private MemberTermsAgreementEntity createCompleteEntity() {
        MemberEntity member = createMemberEntity();
        TermsEntity terms = createTermsEntity();
        
        return MemberTermsAgreementEntity.builder()
                .id(1L)
                .memberEntityId(MemberEntityId.of(member.getId()))
                .termsEntityId(TermsEntityId.of(terms.getId()))
                .agreed(true)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

    private MemberTermsAgreementEntity createDisagreedEntity() {
        MemberEntity member = createMemberEntity();
        TermsEntity terms = createTermsEntity();
        
        return MemberTermsAgreementEntity.builder()
                .id(1L)
                .memberEntityId(MemberEntityId.of(member.getId()))
                .termsEntityId(TermsEntityId.of(terms.getId()))
                .agreed(false)
                .build();
    }

    private MemberTermsAgreementEntity createDeletedEntity(LocalDateTime deletedAt) {
        MemberEntity member = createMemberEntity();
        TermsEntity terms = createTermsEntity();
        
        return MemberTermsAgreementEntity.builder()
                .id(1L)
                .memberEntityId(MemberEntityId.of(member.getId()))
                .termsEntityId(TermsEntityId.of(terms.getId()))
                .agreed(true)
                .deletedAt(deletedAt)
                .build();
    }

    private MemberEntity createMemberEntity() {
        return MemberEntity.builder()
                .id(100L)
                .build();
    }

    private TermsEntity createTermsEntity() {
        return TermsEntity.builder()
                .id(200L)
                .build();
    }

    private MemberTermsAgreement createCompleteMemberTermsAgreement() {
        return MemberTermsAgreement.from(
                1L,
                MemberId.of(100L),
                TermsId.of(200L),
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private MemberTermsAgreement createDisagreedMemberTermsAgreement() {
        return MemberTermsAgreement.from(
                1L,
                MemberId.of(100L),
                TermsId.of(200L),
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private MemberTermsAgreement createDeletedMemberTermsAgreement(LocalDateTime deletedAt) {
        return MemberTermsAgreement.from(
                1L,
                MemberId.of(100L),
                TermsId.of(200L),
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                deletedAt
        );
    }

    private MemberTermsAgreement createMemberTermsAgreementWithNullId() {
        return MemberTermsAgreement.signTerms(
                MemberId.of(100L),
                TermsId.of(200L),
                true
        );
    }
}
