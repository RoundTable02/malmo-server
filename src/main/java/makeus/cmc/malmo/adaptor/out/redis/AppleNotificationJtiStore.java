package makeus.cmc.malmo.adaptor.out.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Apple Server-to-Server Notification의 jti(JWT ID) 중복 처리 방지를 위한 Redis 저장소
 * SETNX + TTL을 사용하여 원자적으로 중복 체크와 저장을 수행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppleNotificationJtiStore {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "apple:notification:jti:";
    private static final Duration TTL = Duration.ofDays(1); // Apple 알림은 24시간 내 재시도

    /**
     * JTI가 이미 처리되었는지 확인하고, 처리되지 않았다면 저장합니다.
     * Redis SETNX를 사용하여 원자적으로 수행됩니다.
     *
     * @param jti Apple 알림의 JWT ID
     * @return true if this is a new JTI (should process), false if already processed (should skip)
     */
    public boolean tryMarkAsProcessed(String jti) {
        String key = KEY_PREFIX + jti;

        // SETNX: 키가 없을 때만 설정 (atomic operation)
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(key, "processed", TTL);

        if (Boolean.TRUE.equals(isNew)) {
            log.debug("New Apple notification JTI: {}", jti);
            return true;
        } else {
            log.info("Duplicate Apple notification JTI detected: {}", jti);
            return false;
        }
    }
}



