package makeus.cmc.malmo.domain.value.type;

/**
 * Apple Sign in with Apple 이메일 포워딩 상태
 * 
 * @see <a href="https://developer.apple.com/documentation/sign_in_with_apple/processing_changes_for_sign_in_with_apple_accounts">Apple Documentation</a>
 */
public enum EmailForwardingStatus {
    /**
     * 이메일 포워딩이 활성화됨
     */
    ENABLED,
    
    /**
     * 이메일 포워딩이 비활성화됨
     */
    DISABLED
}

