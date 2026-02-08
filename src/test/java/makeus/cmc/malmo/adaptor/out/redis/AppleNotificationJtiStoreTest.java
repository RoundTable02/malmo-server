package makeus.cmc.malmo.adaptor.out.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppleNotificationJtiStore 단위 테스트")
class AppleNotificationJtiStoreTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private AppleNotificationJtiStore jtiStore;

    private static final String TEST_JTI = "test-jti-12345";
    private static final String EXPECTED_KEY = "apple:notification:jti:" + TEST_JTI;

    @BeforeEach
    void setUp() {
        jtiStore = new AppleNotificationJtiStore(redisTemplate);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Nested
    @DisplayName("tryMarkAsProcessed 테스트")
    class TryMarkAsProcessedTest {

        @Test
        @DisplayName("새로운 jti인 경우 true를 반환하고 Redis에 저장한다")
        void 새로운_jti인_경우_true를_반환하고_Redis에_저장한다() {
            // given
            given(valueOperations.setIfAbsent(eq(EXPECTED_KEY), eq("processed"), any(Duration.class)))
                    .willReturn(true);

            // when
            boolean result = jtiStore.tryMarkAsProcessed(TEST_JTI);

            // then
            assertThat(result).isTrue();
            verify(valueOperations).setIfAbsent(eq(EXPECTED_KEY), eq("processed"), eq(Duration.ofDays(1)));
        }

        @Test
        @DisplayName("이미 처리된 jti인 경우 false를 반환한다")
        void 이미_처리된_jti인_경우_false를_반환한다() {
            // given
            given(valueOperations.setIfAbsent(eq(EXPECTED_KEY), eq("processed"), any(Duration.class)))
                    .willReturn(false);

            // when
            boolean result = jtiStore.tryMarkAsProcessed(TEST_JTI);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Redis 응답이 null인 경우 false를 반환한다")
        void Redis_응답이_null인_경우_false를_반환한다() {
            // given
            given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                    .willReturn(null);

            // when
            boolean result = jtiStore.tryMarkAsProcessed(TEST_JTI);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("서로 다른 jti는 독립적으로 처리된다")
        void 서로_다른_jti는_독립적으로_처리된다() {
            // given
            String jti1 = "jti-1";
            String jti2 = "jti-2";
            String key1 = "apple:notification:jti:" + jti1;
            String key2 = "apple:notification:jti:" + jti2;

            given(valueOperations.setIfAbsent(eq(key1), eq("processed"), any(Duration.class)))
                    .willReturn(true);
            given(valueOperations.setIfAbsent(eq(key2), eq("processed"), any(Duration.class)))
                    .willReturn(true);

            // when
            boolean result1 = jtiStore.tryMarkAsProcessed(jti1);
            boolean result2 = jtiStore.tryMarkAsProcessed(jti2);

            // then
            assertThat(result1).isTrue();
            assertThat(result2).isTrue();
        }
    }

    @Nested
    @DisplayName("TTL 설정 테스트")
    class TtlTest {

        @Test
        @DisplayName("TTL은 1일로 설정된다")
        void TTL은_1일로_설정된다() {
            // given
            given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                    .willReturn(true);

            // when
            jtiStore.tryMarkAsProcessed(TEST_JTI);

            // then
            verify(valueOperations).setIfAbsent(anyString(), anyString(), eq(Duration.ofDays(1)));
        }
    }
}



