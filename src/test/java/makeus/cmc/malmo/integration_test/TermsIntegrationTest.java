package makeus.cmc.malmo.integration_test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.application.port.out.GenerateTokenPort;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.state.TermsDetailsType;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;
import makeus.cmc.malmo.domain.value.type.TermsType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static makeus.cmc.malmo.adaptor.in.exception.ErrorCode.NO_SUCH_MEMBER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class TermsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private GenerateTokenPort generateTokenPort;

    private String accessToken;

    private MemberEntity member;

    @BeforeEach
    void setup() {
        member = MemberEntity.builder()
                .provider(Provider.KAKAO)
                .providerId("testProviderId")
                .memberRole(MemberRole.MEMBER)
                .memberState(MemberState.ALIVE)
                .email("testEmail@test.com")
                .nickname("nickname")
                .startLoveDate(LocalDate.of(2023, 10, 1))
                .inviteCodeEntityValue(InviteCodeEntityValue.of("testInviteCode"))
                .build();

        em.persist(member);
        em.flush();

        TokenInfo tokenInfo = generateTokenPort.generateToken(member.getId(), member.getMemberRole());

        accessToken = tokenInfo.getAccessToken();
    }

    @Nested
    @DisplayName("약관 조회 기능 검증")
    class GetTermsFeature {
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public static class ResponseDto <T> {
            String requestId;
            boolean success;
            String message;
            T data;
        }

        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public static class ResponseListDto<T> {
            int size;
            Integer page;
            List<T> list;
            Long totalCount;
        }

        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public static class TermsDto {
            TermsType termsType;
            TermsContentDto content;
        }

        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public static class TermsContentDto {
            Long termsId;
            String title;
            List<TermsDetailsDto> details;
            float version;
            @JsonProperty("isRequired")
            boolean isRequired;
        }

        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public static class TermsDetailsDto {
            private TermsDetailsType type;
            private String content;
        }

        @Test
        @DisplayName("정상적인 요청의 경우 약관 조회가 성공한다")
        void 약관조회_성공() throws Exception {
            // when
            MvcResult mvcResult = mockMvc.perform(get("/terms")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            ResponseDto<ResponseListDto<TermsDto>> responseDto = objectMapper.readValue(
                    responseContent,
                    new TypeReference<>() {}
            );
            List<TermsDto> termsList = responseDto.data.list;
            Assertions.assertThat(termsList).isNotEmpty();
            Assertions.assertThat(termsList).hasSize(4);
            Assertions.assertThat(termsList).extracting("termsType")
                    .containsExactlyInAnyOrder(TermsType.PRIVACY_POLICY, TermsType.AGE_VERIFICATION,
                            TermsType.SERVICE_USAGE, TermsType.MARKETING);
            Assertions.assertThat(termsList).extracting("content.version")
                    .containsExactlyInAnyOrder(1.0f, 1.0f, 1.0f, 1.0f);
            Assertions.assertThat(termsList).extracting("content.isRequired")
                    .containsExactlyInAnyOrder(true, true, true, false);
        }

        @Test
        @DisplayName("새 버전의 약관이 존재하는 경우 해당 버전의 약관 조회가 성공한다")
        void 새_버전_약관조회_성공() throws Exception {
            // given
            TermsEntity newTerms = TermsEntity.builder()
                    .title("새로운 개인정보 처리방침")
                    .content("새로운 개인정보 처리방침 내용")
                    .version(1.1f)
                    .isRequired(true)
                    .termsType(TermsType.PRIVACY_POLICY)
                    .build();
            em.persist(newTerms);
            em.flush();

            // when
            MvcResult mvcResult = mockMvc.perform(get("/terms")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            ResponseDto<ResponseListDto<TermsDto>> responseDto = objectMapper.readValue(
                    responseContent,
                    new TypeReference<>() {}
            );
            List<TermsDto> termsList = responseDto.data.list;
            Assertions.assertThat(termsList).isNotEmpty();
            Assertions.assertThat(termsList).hasSize(4);
            Assertions.assertThat(termsList).extracting("termsType")
                    .containsExactlyInAnyOrder(TermsType.PRIVACY_POLICY, TermsType.AGE_VERIFICATION,
                            TermsType.SERVICE_USAGE, TermsType.MARKETING);
            Assertions.assertThat(termsList).extracting("content.version")
                    .containsExactlyInAnyOrder(1.1f, 1.0f, 1.0f, 1.0f);
            Assertions.assertThat(termsList).extracting("content.isRequired")
                    .containsExactlyInAnyOrder(true, true, true, false);
        }
    }
}
