package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.MemberTermsAgreementEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.TermsEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberTermsAgreementMapper;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.id.TermsId;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("Entity를 Domain으로 변환한다")
    void toDomain() {
        // given
        MemberTermsAgreementEntity entity = createCompleteEntity();

        // when
        MemberTermsAgreement domain = memberTermsAgreementMapper.toDomain(entity);

        // then
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getMemberId().getValue()).isEqualTo(entity.getMemberEntityId().getValue());
        assertThat(domain.getTermsId().getValue()).isEqualTo(entity.getTermsEntityId().getValue());
        assertThat(domain.isAgreed()).isEqualTo(entity.isAgreed());
        assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(domain.getModifiedAt()).isEqualTo(entity.getModifiedAt());
        assertThat(domain.getDeletedAt()).isEqualTo(entity.getDeletedAt());
    }

    @Test
    @DisplayName("Domain을 Entity로 변환한다")
    void toEntity() {
        // given
        MemberTermsAgreement domain = createCompleteDomain();

        // when
        MemberTermsAgreementEntity entity = memberTermsAgreementMapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getMemberEntityId().getValue()).isEqualTo(domain.getMemberId().getValue());
        assertThat(entity.getTermsEntityId().getValue()).isEqualTo(domain.getTermsId().getValue());
        assertThat(entity.isAgreed()).isEqualTo(domain.isAgreed());
        assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
        assertThat(entity.getModifiedAt()).isEqualTo(domain.getModifiedAt());
        assertThat(entity.getDeletedAt()).isEqualTo(domain.getDeletedAt());
    }

    private MemberTermsAgreementEntity createCompleteEntity() {
        LocalDateTime now = LocalDateTime.now();
        return MemberTermsAgreementEntity.builder()
                .id(1L)
                .memberEntityId(MemberEntityId.of(100L))
                .termsEntityId(TermsEntityId.of(200L))
                .agreed(true)
                .createdAt(now)
                .modifiedAt(now)
                .deletedAt(null)
                .build();
    }

    private MemberTermsAgreement createCompleteDomain() {
        LocalDateTime now = LocalDateTime.now();
        return MemberTermsAgreement.from(
                1L,
                MemberId.of(100L),
                TermsId.of(200L),
                true,
                now,
                now,
                null
        );
    }
}
