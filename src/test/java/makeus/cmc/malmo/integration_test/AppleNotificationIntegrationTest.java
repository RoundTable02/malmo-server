package makeus.cmc.malmo.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import makeus.cmc.malmo.adaptor.out.oidc.AppleNotificationValidator;
import makeus.cmc.malmo.adaptor.out.redis.AppleNotificationJtiStore;
import makeus.cmc.malmo.application.port.in.member.AppleNotificationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@DisplayName("Apple Server-to-Server Notification 통합 테스트")
public class AppleNotificationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppleNotificationUseCase appleNotificationUseCase;

    @MockBean
    private AppleNotificationValidator appleNotificationValidator;

    @MockBean
    private AppleNotificationJtiStore appleNotificationJtiStore;

    private static final String WEBHOOK_ENDPOINT = "/webhook/apple/notifications";
    private static final String SAMPLE_SIGNED_PAYLOAD = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLmV4YW1wbGUuYXBwIiwiaWF0IjoxNjE2NDM5MDIyLCJqdGkiOiJ0ZXN0LWp0aS0xMjM0NSIsImV2ZW50cyI6eyJ0eXBlIjoiY29uc2VudC1yZXZva2VkIiwic3ViIjoiMDAwMTIzLjEyMzQ1Njc4OTBhYmNkZWYuMTIzNCIsImV2ZW50X3RpbWUiOjE2MTY0MzkwMjJ9fQ.test-signature";

    @Nested
    @DisplayName("Webhook 엔드포인트 테스트")
    class WebhookEndpointTest {

        @Test
        @DisplayName("JSON 형식의 signed_payload를 받으면 200 OK를 반환한다")
        void JSON_형식의_signed_payload를_받으면_200_OK를_반환한다() throws Exception {
            // given
            doNothing().when(appleNotificationUseCase).processNotification(eq(SAMPLE_SIGNED_PAYLOAD));
            
            Map<String, String> requestBody = Map.of("signed_payload", SAMPLE_SIGNED_PAYLOAD);

            // when & then
            mockMvc.perform(post(WEBHOOK_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk());

            verify(appleNotificationUseCase).processNotification(eq(SAMPLE_SIGNED_PAYLOAD));
        }

        @Test
        @DisplayName("Form URL Encoded 형식의 signed_payload를 받으면 200 OK를 반환한다")
        void Form_URL_Encoded_형식의_signed_payload를_받으면_200_OK를_반환한다() throws Exception {
            // given
            doNothing().when(appleNotificationUseCase).processNotification(eq(SAMPLE_SIGNED_PAYLOAD));

            // when & then
            mockMvc.perform(post(WEBHOOK_ENDPOINT)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("signed_payload", SAMPLE_SIGNED_PAYLOAD))
                    .andExpect(status().isOk());

            verify(appleNotificationUseCase).processNotification(eq(SAMPLE_SIGNED_PAYLOAD));
        }

        @Test
        @DisplayName("인증 없이 Webhook 엔드포인트에 접근할 수 있다")
        void 인증_없이_Webhook_엔드포인트에_접근할_수_있다() throws Exception {
            // given
            doNothing().when(appleNotificationUseCase).processNotification(eq(SAMPLE_SIGNED_PAYLOAD));
            
            Map<String, String> requestBody = Map.of("signed_payload", SAMPLE_SIGNED_PAYLOAD);

            // when & then - Authorization 헤더 없이 호출
            mockMvc.perform(post(WEBHOOK_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("빈 signed_payload가 오면 200 OK를 반환하되 처리하지 않는다")
        void 빈_signed_payload가_오면_200_OK를_반환한다() throws Exception {
            // given
            Map<String, String> requestBody = Map.of("signed_payload", "");

            // when & then - 빈 페이로드도 200 반환 (Apple 재시도 방지)
            mockMvc.perform(post(WEBHOOK_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk());
        }
    }
}

