package makeus.cmc.malmo.application.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import makeus.cmc.malmo.adaptor.out.oidc.AppleNotificationValidator;
import makeus.cmc.malmo.adaptor.out.oidc.AppleNotificationValidator.AppleNotificationClaims;
import makeus.cmc.malmo.adaptor.out.redis.AppleNotificationJtiStore;
import makeus.cmc.malmo.application.helper.member.MemberCommandHelper;
import makeus.cmc.malmo.application.helper.member.MemberQueryHelper;
import makeus.cmc.malmo.application.port.in.member.AppleNotificationUseCase;
import makeus.cmc.malmo.application.port.out.member.UnlinkApplePort;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.type.EmailForwardingStatus;
import makeus.cmc.malmo.domain.value.type.Provider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Apple Server-to-Server Notification 처리 서비스
 *
 * @see <a href="https://developer.apple.com/documentation/sign_in_with_apple/processing_changes_for_sign_in_with_apple_accounts">Apple Documentation</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppleNotificationService implements AppleNotificationUseCase {

    private final AppleNotificationValidator notificationValidator;
    private final AppleNotificationJtiStore jtiStore;
    private final MemberQueryHelper memberQueryHelper;
    private final MemberCommandHelper memberCommandHelper;
    private final UnlinkApplePort unlinkApplePort;

    private static final String EVENT_CONSENT_REVOKED = "consent-revoked";
    private static final String EVENT_ACCOUNT_DELETE = "account-delete";
    private static final String EVENT_EMAIL_ENABLED = "email-enabled";
    private static final String EVENT_EMAIL_DISABLED = "email-disabled";

    @Override
    @Transactional
    public void processNotification(String signedPayload) {
        // 1. JWT 검증 및 파싱 (JWKS 서명 검증 포함)
        AppleNotificationClaims claims = notificationValidator.validateAndParse(signedPayload);

        // 2. JTI 중복 체크 (이미 처리된 알림이면 스킵)
        if (!jtiStore.tryMarkAsProcessed(claims.getJti())) {
            log.info("Skipping duplicate Apple notification: jti={}", claims.getJti());
            return;
        }

        // 3. 사용자 조회
        Optional<Member> memberOpt = memberQueryHelper.getMemberByProviderId(
                Provider.APPLE, claims.getSub()
        );

        if (memberOpt.isEmpty()) {
            log.warn("Apple notification for unknown user: sub={}", claims.getSub());
            return;
        }

        Member member = memberOpt.get();

        // 4. 이벤트 타입별 처리
        switch (claims.getEventType()) {
            case EVENT_CONSENT_REVOKED:
                handleConsentRevoked(member);
                break;
            case EVENT_ACCOUNT_DELETE:
                handleAccountDelete(member);
                break;
            case EVENT_EMAIL_ENABLED:
            case EVENT_EMAIL_DISABLED:
                handleEmailChange(member, claims);
                break;
            default:
                log.warn("Unknown Apple event type: {}", claims.getEventType());
        }
    }

    /**
     * 사용자가 앱 연결 해제 시 처리
     * - 소프트 삭제
     * - 토큰 무효화
     * - Apple refresh token revoke (있는 경우)
     */
    private void handleConsentRevoked(Member member) {
        log.info("User revoked consent: memberId={}", member.getId());

        // 토큰 무효화 (refreshToken, firebaseToken)
        member.logOut();

        // 소프트 삭제
        member.delete();

        memberCommandHelper.saveMember(member);

        // Apple refresh token revoke (있는 경우)
        revokeAppleTokenIfExists(member);
    }

    /**
     * Apple 계정 삭제 시 처리
     * - 소프트 삭제
     * - 토큰 무효화
     */
    private void handleAccountDelete(Member member) {
        log.info("Apple account deleted: memberId={}", member.getId());

        // 토큰 무효화
        member.logOut();

        // 소프트 삭제
        member.delete();

        memberCommandHelper.saveMember(member);
    }

    /**
     * 이메일 포워딩 변경 시 처리
     * - email-enabled: 상태를 ENABLED로 변경하고 새 이메일이 있으면 업데이트
     * - email-disabled: 상태를 DISABLED로 변경
     */
    private void handleEmailChange(Member member, AppleNotificationClaims claims) {
        log.info("Email forwarding changed: memberId={}, event={}, email={}",
                member.getId(), claims.getEventType(), claims.getEmail());

        if (EVENT_EMAIL_ENABLED.equals(claims.getEventType())) {
            // 상태를 ENABLED로 변경
            member.updateEmailForwardingStatus(EmailForwardingStatus.ENABLED);
            // 새 이메일이 있으면 업데이트
            if (claims.getEmail() != null) {
                member.updateEmail(claims.getEmail());
                log.info("Member email updated: memberId={}, newEmail={}", member.getId(), claims.getEmail());
            }
            memberCommandHelper.saveMember(member);
        } else if (EVENT_EMAIL_DISABLED.equals(claims.getEventType())) {
            // 상태를 DISABLED로 변경
            member.updateEmailForwardingStatus(EmailForwardingStatus.DISABLED);
            memberCommandHelper.saveMember(member);
            log.info("Member email forwarding disabled: memberId={}", member.getId());
        }
    }

    /**
     * Apple refresh token이 있으면 revoke 호출
     */
    private void revokeAppleTokenIfExists(Member member) {
        String oauthToken = member.getOauthToken();
        if (oauthToken != null && !oauthToken.isBlank()) {
            try {
                unlinkApplePort.unlink(oauthToken);
                log.info("Apple refresh token revoked: memberId={}", member.getId());
            } catch (Exception e) {
                // revoke 실패해도 사용자 삭제는 진행되어야 함
                log.error("Failed to revoke Apple refresh token: memberId={}, error={}",
                        member.getId(), e.getMessage());
            }
        }
    }
}



