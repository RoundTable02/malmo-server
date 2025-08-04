package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.member.UpdateTermsAgreementUseCase;
import makeus.cmc.malmo.application.service.helper.terms.TermsCommandHelper;
import makeus.cmc.malmo.application.service.helper.terms.TermsQueryHelper;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.id.TermsId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TermsAgreementService implements UpdateTermsAgreementUseCase {

    private final TermsQueryHelper termsQueryHelper;
    private final TermsCommandHelper termsCommandHelper;

    @Override
    @Transactional
    @CheckValidMember
    public TermsAgreementResponse updateTermsAgreement(TermsAgreementCommand command) {
        List<TermsDto> termsList = command.getTerms();
        Long memberId = command.getMemberId();

        List<TermsDto> termsResult = new ArrayList<>();
        for (TermsDto terms : termsList) {
            MemberTermsAgreement memberTermsAgreement = termsQueryHelper.getTermsAgreementOrThrow(
                    MemberId.of(memberId),
                    TermsId.of(terms.getTermsId())
            );
            memberTermsAgreement.updateAgreement(terms.getIsAgreed());
            termsCommandHelper.saveMemberTermsAgreement(memberTermsAgreement);

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
