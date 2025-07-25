package makeus.cmc.malmo.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import makeus.cmc.malmo.adaptor.in.web.controller.SignUpController;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.MemberTermsAgreementEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.application.port.out.GenerateTokenPort;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class MemberIntegrationTest {

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
                .inviteCodeEntityValue(InviteCodeEntityValue.of("testInviteCode"))
                .build();

        em.persist(member);
        em.flush();

        TokenInfo tokenInfo = generateTokenPort.generateToken(member.getId(), member.getMemberRole());

        accessToken = tokenInfo.getAccessToken();
    }

    @Nested
    @DisplayName("회원가입 기능 검증")
    class SignUpFeature {
        @Test
        @DisplayName("정상적인 요청의 경우 회원가입이 성공한다")
        void 회원가입_성공() throws Exception {
            List<SignUpController.TermsDto> terms = List.of(
                    MemberRequestDtoFactory.createTermsDto(1L, true),
                    MemberRequestDtoFactory.createTermsDto(2L, true),
                    MemberRequestDtoFactory.createTermsDto(3L, true),
                    MemberRequestDtoFactory.createTermsDto(4L, true)
            );

            mockMvc.perform(post("/members/onboarding")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createSignUpRequestDto(terms,
                                            "테스트닉네임",
                                            LocalDate.of(2023, 10, 1))
                            )))
                    .andExpect(status().isOk());

            MemberEntity savedMember = em.find(MemberEntity.class, member.getId());

            Assertions.assertThat(savedMember.getNickname()).isEqualTo("테스트닉네임");
            Assertions.assertThat(savedMember.getStartLoveDate()).isEqualTo(LocalDate.of(2023, 10, 1));

            List<MemberTermsAgreementEntity> agreements = em.createQuery(
                            "SELECT t FROM MemberTermsAgreementEntity t WHERE t.memberEntityId.value = :memberId",
                            MemberTermsAgreementEntity.class)
                    .setParameter("memberId", member.getId())
                    .getResultList();

            Assertions.assertThat(agreements).hasSize(4);
            Assertions.assertThat(agreements)
                    .allMatch(MemberTermsAgreementEntity::isAgreed)
                    .extracting(agreement -> agreement.getTermsEntityId().getValue())
                    .containsExactlyInAnyOrder(1L, 2L, 3L, 4L);
        }

        // 닉네임 길이 경계값 테스트
        @Test
        @DisplayName("닉네임 10자인 경우 회원가입이 성공한다")
        void 회원가입_닉네임_길이_경계값_성공() throws Exception {
            List<SignUpController.TermsDto> terms = List.of(
                    MemberRequestDtoFactory.createTermsDto(1L, true),
                    MemberRequestDtoFactory.createTermsDto(2L, true),
                    MemberRequestDtoFactory.createTermsDto(3L, true),
                    MemberRequestDtoFactory.createTermsDto(4L, true)
            );

            mockMvc.perform(post("/members/onboarding")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createSignUpRequestDto(terms,
                                            "테스트닉네임1234", // 10자
                                            LocalDate.of(2023, 10, 1))
                            )))
                    .andExpect(status().isOk());
        }

        // 닉네임 길이 초과 실패 테스트
        @Test
        @DisplayName("닉네임이 11자 이상인 경우 회원가입이 실패한다")
        void 회원가입_닉네임_길이_초과_실패() throws Exception {
            List<SignUpController.TermsDto> terms = List.of(
                    MemberRequestDtoFactory.createTermsDto(1L, true),
                    MemberRequestDtoFactory.createTermsDto(2L, true),
                    MemberRequestDtoFactory.createTermsDto(3L, true),
                    MemberRequestDtoFactory.createTermsDto(4L, true)
            );

            mockMvc.perform(post("/members/onboarding")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createSignUpRequestDto(terms,
                                            "테스트닉네임12345", // 11자
                                            LocalDate.of(2023, 10, 1))
                            )))
                    .andExpect(status().isBadRequest());
        }

        // 닉네임 특수문자 실패 테스트
        @Test
        @DisplayName("닉네임에 특수문자가 포함된 경우 회원가입이 실패한다")
        void 회원가입_닉네임_특수문자_포함_실패() throws Exception {
            List<SignUpController.TermsDto> terms = List.of(
                    MemberRequestDtoFactory.createTermsDto(1L, true),
                    MemberRequestDtoFactory.createTermsDto(2L, true),
                    MemberRequestDtoFactory.createTermsDto(3L, true),
                    MemberRequestDtoFactory.createTermsDto(4L, true)
            );

            mockMvc.perform(post("/members/onboarding")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createSignUpRequestDto(terms,
                                            "테스트닉네임!@#", // 특수문자 포함
                                            LocalDate.of(2023, 10, 1))
                            )))
                    .andExpect(status().isBadRequest());
        }

        // 시작일 경계값 테스트
        @Test
        @DisplayName("시작일이 오늘 날짜인 경우 회원가입이 성공한다")
        void 회원가입_시작일_오늘_성공() throws Exception {
            List<SignUpController.TermsDto> terms = List.of(
                    MemberRequestDtoFactory.createTermsDto(1L, true),
                    MemberRequestDtoFactory.createTermsDto(2L, true),
                    MemberRequestDtoFactory.createTermsDto(3L, true),
                    MemberRequestDtoFactory.createTermsDto(4L, true)
            );

            LocalDate today = LocalDate.now();

            mockMvc.perform(post("/members/onboarding")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createSignUpRequestDto(terms,
                                            "테스트닉네임",
                                            today)
                            )))
                    .andExpect(status().isOk());
        }

        // 시작일 경계값 이후 실패 테스트
        @Test
        @DisplayName("시작일이 미래 날짜인 경우 회원가입이 실패한다")
        void 회원가입_시작일_미래_실패() throws Exception {
            List<SignUpController.TermsDto> terms = List.of(
                    MemberRequestDtoFactory.createTermsDto(1L, true),
                    MemberRequestDtoFactory.createTermsDto(2L, true),
                    MemberRequestDtoFactory.createTermsDto(3L, true),
                    MemberRequestDtoFactory.createTermsDto(4L, true)
            );

            LocalDate futureDate = LocalDate.now().plusDays(1);

            mockMvc.perform(post("/members/onboarding")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createSignUpRequestDto(terms,
                                            "테스트닉네임",
                                            futureDate)
                            )))
                    .andExpect(status().isBadRequest());
        }
    }

}
