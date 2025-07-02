package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.MemberTermsAgreementEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsEntity;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import org.springframework.stereotype.Component;

@Component
public class MemberTermsAgreementMapper {

    public MemberTermsAgreement toDomain(MemberTermsAgreementEntity entity) {
        return MemberTermsAgreement.builder()
                .id(entity.getId())
                .memberId(entity.getMember().getId())
                .termsId(entity.getTerms().getId())
                .agreed(entity.isAgreed())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public MemberTermsAgreementEntity toEntity(MemberTermsAgreement domain,
                                               MemberEntity memberEntity,
                                               TermsEntity termsEntity) {
        return MemberTermsAgreementEntity.builder()
                .id(domain.getId())
                .member(memberEntity)
                .terms(termsEntity)
                .agreed(domain.isAgreed())
                .createdAt(domain.getCreatedAt())
                .modifiedAt(domain.getModifiedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}