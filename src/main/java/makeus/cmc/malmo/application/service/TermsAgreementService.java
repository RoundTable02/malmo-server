package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.TermsNotFoundException;
import makeus.cmc.malmo.application.port.in.UpdateTermsAgreementUseCase;
import makeus.cmc.malmo.application.port.out.LoadTermsAgreementPort;
import makeus.cmc.malmo.application.port.out.SaveMemberTermsAgreement;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.model.value.TermsId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TermsAgreementService implements UpdateTermsAgreementUseCase {

    private final LoadTermsAgreementPort termsAgreementPort;
    private final SaveMemberTermsAgreement saveMemberTermsAgreement;

    @Override
    @Transactional
    public TermsAgreementResponse updateTermsAgreement(TermsAgreementCommand command) {
        List<TermsDto> termsList = command.getTerms();
        Long memberId = command.getMemberId();

        List<TermsDto> termsResult = new ArrayList<>();
        for (TermsDto terms : termsList) {
            MemberTermsAgreement memberTermsAgreement = termsAgreementPort.loadTermsAgreementByMemberIdAndTermsId(
                    MemberId.of(memberId), TermsId.of(terms.getTermsId())
            ).orElseThrow(TermsNotFoundException::new);
            memberTermsAgreement.updateAgreement(terms.getIsAgreed());
            saveMemberTermsAgreement.saveMemberTermsAgreement(memberTermsAgreement);

            termsResult.add(TermsDto.builder()
                    .termsId(terms.getTermsId())
                    .isAgreed(memberTermsAgreement.isAgreed())
                    .build());
        }

        return TermsAgreementResponse.builder()
                .terms(termsResult)
                .build();
    }
}
