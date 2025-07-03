package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.MemberTermsAgreementEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.TermsEntityId;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.model.value.TermsId;
import org.springframework.stereotype.Component;

@Component
public class MemberTermsAgreementMapper {

    public MemberTermsAgreement toDomain(MemberTermsAgreementEntity entity) {
        return MemberTermsAgreement.builder()
                .id(entity.getId())
                .memberId(MemberId.of(entity.getMemberEntityId().getValue()))
                .termsId(TermsId.of(entity.getTermsEntityId().getValue()))
                .agreed(entity.isAgreed())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
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