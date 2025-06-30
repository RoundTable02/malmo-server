package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.TermsNotFoundException;
import makeus.cmc.malmo.application.port.in.SignUpUseCase;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.LoadTermsPort;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.application.port.out.SaveMemberTermsAgreement;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.model.terms.Terms;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignUpService implements SignUpUseCase {

    private final LoadMemberPort loadMemberPort;
    private final SaveMemberPort saveMemberPort;

    private final LoadTermsPort loadTermsPort;
    private final SaveMemberTermsAgreement saveMemberTermsAgreement;

    @Override
    @Transactional
    public SignUpResponse signUp(SignUpCommand command) {
        // 멤버 조회 및 저장
        Member member = loadMemberPort.loadMemberById(command.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        member.signUp(command.getNickname());

        Member savedMember = saveMemberPort.saveMember(member);

        // 약관 동의 처리
        command.getTerms().forEach(term -> {
            Terms terms = loadTermsPort.loadTermsById(term.getTermsId())
                    .orElseThrow(TermsNotFoundException::new);

            MemberTermsAgreement memberTermsAgreement = MemberTermsAgreement.signTerms(
                    savedMember.getId(),
                    terms.getId(),
                    term.getIsAgreed());
            saveMemberTermsAgreement.saveMemberTermsAgreement(memberTermsAgreement, member, terms);
        });

        // 커플 코드 생성

        return null;
    }
}
