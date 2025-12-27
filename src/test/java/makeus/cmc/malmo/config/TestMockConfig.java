package makeus.cmc.malmo.config;

import makeus.cmc.malmo.adaptor.out.oidc.AppleNotificationValidator;
import makeus.cmc.malmo.adaptor.out.redis.AppleNotificationJtiStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

/**
 * 테스트 환경에서 네트워크 의존성이 있는 빈들을 Mock으로 대체합니다.
 * 
 * AppleNotificationValidator는 생성자에서 Apple JWKS URI에 연결을 시도하므로
 * 테스트 환경에서는 Mock으로 대체합니다.
 * 
 * @Profile("test") 덕분에 test 프로파일에서 자동으로 로드됩니다.
 */
@Configuration
@Profile("test")
public class TestMockConfig {

    @Bean
    @Primary
    public AppleNotificationValidator appleNotificationValidator() {
        return mock(AppleNotificationValidator.class);
    }

    @Bean
    @Primary
    public AppleNotificationJtiStore appleNotificationJtiStore() {
        return mock(AppleNotificationJtiStore.class);
    }
}

