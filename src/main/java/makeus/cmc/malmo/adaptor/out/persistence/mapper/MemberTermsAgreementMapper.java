package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.MemberTermsAgreementEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.TermsEntityId;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.id.TermsId;
import org.springframework.stereotype.Component;

@Component
public class MemberTermsAgreementMapper {

    public MemberTermsAgreement toDomain(MemberTermsAgreementEntity entity) {
        return MemberTermsAgreement.from(
                entity.getId(),
                MemberId.of(entity.getMemberEntityId().getValue()),
                TermsId.of(entity.getTermsEntityId().getValue()),
                entity.isAgreed(),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getDeletedAt()
        );
    }

    public MemberTermsAgreementEntity toEntity(MemberTermsAgreement domain) {
        return MemberTermsAgreementEntity.builder()
                .id(domain.getId())
                .memberEntityId(MemberEntityId.of(domain.getMemberId().getValue()))
                .termsEntityId(TermsEntityId.of(domain.getTermsId().getValue()))
                .agreed(domain.isAgreed())
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}