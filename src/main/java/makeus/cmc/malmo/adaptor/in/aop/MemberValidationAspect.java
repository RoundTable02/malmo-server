package makeus.cmc.malmo.adaptor.in.aop;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.domain.value.id.MemberId;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class MemberValidationAspect {

    private final MemberQueryHelper memberQueryHelper;

    @Before("@annotation(CheckCoupleMember)")
    public void checkCoupleMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AuthenticationCredentialsNotFoundException("인증된 사용자를 찾을 수 없습니다.");
        }

        Long memberId = Long.valueOf(user.getUsername());
        // ALIVE 상태의 커플, ALIVE 상태의 멤버인지 확인
        memberQueryHelper.isMemberCouple(MemberId.of(memberId));
    }

    @Before("@annotation(CheckValidMember)")
    public void checkValidMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AuthenticationCredentialsNotFoundException("인증된 사용자를 찾을 수 없습니다.");
        }

        Long memberId = Long.valueOf(user.getUsername());
        // ALIVE 상태의 멤버인지 확인
        memberQueryHelper.isMemberValid(MemberId.of(memberId));
    }
}

