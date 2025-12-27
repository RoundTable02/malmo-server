package makeus.cmc.malmo.application.port.in.member;

/**
 * Apple Server-to-Server Notification 처리를 위한 UseCase
 * 
 * @see <a href="https://developer.apple.com/documentation/sign_in_with_apple/processing_changes_for_sign_in_with_apple_accounts">Apple Documentation</a>
 */
public interface AppleNotificationUseCase {

    /**
     * Apple로부터 수신한 서명된 페이로드를 처리합니다.
     * 
     * @param signedPayload Apple이 보낸 JWT 형식의 서명된 페이로드
     */
    void processNotification(String signedPayload);
}



