package makeus.cmc.malmo.adaptor.out.persistence.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.MemberTermsAgreementEntity;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import org.springframework.stereotype.Component;

@Component
public class MemberTermsAgreementMapper {

    private final MemberMapper memberMapper;
    private final TermsMapper termsMapper;

    public MemberTermsAgreementMapper(MemberMapper memberMapper, TermsMapper termsMapper) {
        this.memberMapper = memberMapper;
        this.termsMapper = termsMapper;
    }

    public MemberTermsAgreement toDomain(MemberTermsAgreementEntity entity) {
        return MemberTermsAgreement.builder()
                .id(entity.getId())
                .member(memberMapper.toDomain(entity.getMember()))
                .terms(termsMapper.toDomain(entity.getTerms()))
                .agreed(entity.isAgreed())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public MemberTermsAgreementEntity toEntity(MemberTermsAgreement domain) {
        return MemberTermsAgreementEntity.builder()
                .id(domain.getId())
                .member(memberMapper.toEntity(domain.getMember()))
                .terms(termsMapper.toEntity(domain.getTerms()))
                .agreed(domain.isAgreed())
                .build();
    }
}