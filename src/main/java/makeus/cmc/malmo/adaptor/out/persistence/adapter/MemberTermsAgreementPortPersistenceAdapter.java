package makeus.cmc.malmo.adaptor.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.MemberTermsAgreementEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.TermsEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberTermsAgreementMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.terms.MemberTermsAgreementRepository;
import makeus.cmc.malmo.application.port.out.terms.LoadTermsAgreementPort;
import makeus.cmc.malmo.application.port.out.terms.SaveMemberTermsAgreementPort;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.id.TermsId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberTermsAgreementPortPersistenceAdapter implements SaveMemberTermsAgreementPort, LoadTermsAgreementPort {

    private final MemberTermsAgreementRepository memberTermsAgreementRepository;
    private final MemberTermsAgreementMapper memberTermsAgreementMapper;

    @Override
    public void saveMemberTermsAgreement(MemberTermsAgreement memberTermsAgreement) {
        MemberTermsAgreementEntity entity = memberTermsAgreementMapper.toEntity(memberTermsAgreement);

        memberTermsAgreementRepository.save(entity);
    }

    @Override
    public Optional<MemberTermsAgreement> loadTermsAgreementByMemberIdAndTermsId(MemberId memberId, TermsId termsId) {
        return memberTermsAgreementRepository.findByMemberEntityIdAndTermsEntityId(
                        MemberEntityId.of(memberId.getValue()), TermsEntityId.of(termsId.getValue()))
                .map(memberTermsAgreementMapper::toDomain);
    }
}
