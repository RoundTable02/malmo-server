package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.in.aop.CheckValidMember;
import makeus.cmc.malmo.application.port.in.SignUpUseCase;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.application.service.helper.member.MemberCommandHelper;
import makeus.cmc.malmo.application.service.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.service.helper.terms.TermsCommandHelper;
import makeus.cmc.malmo.application.service.helper.terms.TermsQueryHelper;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.model.terms.Terms;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.id.TermsId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpService implements SignUpUseCase {

    private final MemberQueryHelper memberQueryHelper;
    private final MemberCommandHelper memberCommandHelper;

    private final TermsQueryHelper termsQueryHelper;
    private final TermsCommandHelper termsCommandHelper;

    @Override
    @Transactional
    @CheckValidMember
    public void signUp(SignUpCommand command) {
        Member member = memberQueryHelper.getMemberByIdOrThrow(MemberId.of(command.getMemberId()));
        member.signUp(command.getNickname(), command.getLoveStartDate());
        memberCommandHelper.saveMember(member);

        command.getTerms().forEach(termsCommand -> {
            Terms terms = termsQueryHelper.getTermsByIdOrThrow(termsCommand.getTermsId());
            MemberTermsAgreement memberTermsAgreement = MemberTermsAgreement.signTerms(
                    MemberId.of(member.getId()),
                    TermsId.of(terms.getId()),
                    termsCommand.getIsAgreed());

            termsCommandHelper.saveMemberTermsAgreement(memberTermsAgreement);
        });
    }
}
