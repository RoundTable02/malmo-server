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
    private final TermsMapper termsMapper;
    private final MemberMapper memberMapper;

    @Override
    public void saveMemberTermsAgreement(MemberTermsAgreement memberTermsAgreement, Member member, Terms terms) {
        TermsEntity termsEntity = termsMapper.toEntity(terms);
        MemberEntity memberEntity = memberMapper.toEntity(member);

        MemberTermsAgreementEntity entity = memberTermsAgreementMapper.toEntity(
                memberTermsAgreement,
                memberEntity,
                termsEntity);

        memberTermsAgreementRepository.save(entity);
    }
}
