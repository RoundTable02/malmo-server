package makeus.cmc.malmo.adaptor.out.persistence;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.MemberTermsAgreementEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberMapper;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberTermsAgreementMapper;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.TermsMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.MemberTermsAgreementRepository;
import makeus.cmc.malmo.application.port.out.SaveMemberTermsAgreement;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.model.terms.Terms;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberTermsAgreementPersistenceAdapter implements SaveMemberTermsAgreement {

    private final MemberTermsAgreementRepository memberTermsAgreementRepository;
    private final MemberTermsAgreementMapper memberTermsAgreementMapper;

    @Override
    public void saveMemberTermsAgreement(MemberTermsAgreement memberTermsAgreement) {
        MemberTermsAgreementEntity entity = memberTermsAgreementMapper.toEntity(memberTermsAgreement);

        memberTermsAgreementRepository.save(entity);
    }
}
