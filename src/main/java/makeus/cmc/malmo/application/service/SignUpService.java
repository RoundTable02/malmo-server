package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.in.SignUpUseCase;
import makeus.cmc.malmo.application.port.out.*;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.CoupleCodeDomainService;
import makeus.cmc.malmo.domain.service.MemberDomainService;
import makeus.cmc.malmo.domain.service.TermsAgreementDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignUpService implements SignUpUseCase {

    private final MemberDomainService memberDomainService;
    private final SaveMemberPort saveMemberPort;
    private final TermsAgreementDomainService termsAgreementDomainService;
    private final CoupleCodeDomainService coupleCodeDomainService;

    @Override
    @Transactional
    public SignUpResponse signUp(SignUpCommand command) {
        Member member = memberDomainService.getMemberById(MemberId.of(command.getMemberId()));
        member.signUp(command.getNickname());

        saveMemberPort.saveMember(member);

        List<TermsAgreementDomainService.TermAgreementInput> agreementInputs = command.getTerms().stream()
                .map(term -> new TermsAgreementDomainService.TermAgreementInput(term.getTermsId(), term.getIsAgreed()))
                .toList();
        termsAgreementDomainService.processAgreements(MemberId.of(member.getId()), agreementInputs);

        CoupleCode coupleCode = coupleCodeDomainService.generateAndSaveUniqueCoupleCode(member, command.getLoveStartDate());

        return SignUpResponse.builder()
                .coupleCode(coupleCode.getInviteCode())
                .build();
    }
}
