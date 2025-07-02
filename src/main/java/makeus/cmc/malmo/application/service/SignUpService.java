package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.InviteCodeGenerateFailedException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.adaptor.out.persistence.exception.TermsNotFoundException;
import makeus.cmc.malmo.application.port.in.SignUpUseCase;
import makeus.cmc.malmo.application.port.out.*;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.terms.MemberTermsAgreement;
import makeus.cmc.malmo.domain.model.terms.Terms;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.model.value.TermsId;
import org.springframework.dao.DataIntegrityViolationException;
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

    private final GenerateInviteCodePort generateInviteCodePort;
    private final SaveCoupleCodePort saveCoupleCodePort;

    private static final int MAX_RETRY = 10;

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
                    MemberId.of(savedMember.getId()),
                    TermsId.of(terms.getId()),
                    term.getIsAgreed());
            saveMemberTermsAgreement.saveMemberTermsAgreement(memberTermsAgreement);
        });

        // 커플 코드 생성
        int retryCount = 0;
        while (retryCount < MAX_RETRY) {
            String inviteCode = generateInviteCodePort.generateInviteCode();
            try {
                CoupleCode coupleCode = savedMember.generateCoupleCode(inviteCode, command.getLoveStartDate());
                saveCoupleCodePort.saveCoupleCode(coupleCode);
                return SignUpResponse.builder()
                        .coupleCode(inviteCode)
                        .build();
            } catch (DataIntegrityViolationException e) {
                // UNIQUE 제약 조건 위반 시 재시도
                retryCount++;
            }
        }

        throw new InviteCodeGenerateFailedException("커플 코드 생성에 실패했습니다. 재시도 횟수를 초과했습니다.");
    }
}
