package makeus.cmc.malmo.adaptor.in.aop;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.domain.model.value.MemberId;
import makeus.cmc.malmo.domain.service.MemberDomainValidationService;
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
public class CoupleMemberValidationAspect {

    private final MemberDomainValidationService memberValidationService;

    @Before("@annotation(CheckCoupleMember)")
    public void checkCoupleMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AuthenticationCredentialsNotFoundException("인증된 사용자를 찾을 수 없습니다.");
        }

        Long memberId = Long.valueOf(user.getUsername());
        memberValidationService.isMemberCouple(MemberId.of(memberId));
    }
}

