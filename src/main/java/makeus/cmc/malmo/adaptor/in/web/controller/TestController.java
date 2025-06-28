package makeus.cmc.malmo.adaptor.in.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    // 인증 없이 호출 가능한 테스트 API
    @GetMapping("/test")
    public String test() {
        return "API Test Success";
    }

    // 인증이 필요한 API (USER, ADMIN 모두 접근 가능)
    @GetMapping("/me")
    public String getMyInfo(@AuthenticationPrincipal User user) {
        log.info("Current User ID: {}", user.getUsername());
        log.info("Current User Authorities: {}", user.getAuthorities());
        return "My Member ID is " + user.getUsername();
    }

    // ADMIN 권한이 필요한 API
    @GetMapping("/admin/test")
    public String adminTest() {
        return "Admin Test Success";
    }


}
