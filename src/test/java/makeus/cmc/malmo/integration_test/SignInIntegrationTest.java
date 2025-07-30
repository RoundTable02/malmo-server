package makeus.cmc.malmo.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.adaptor.out.oidc.AppleOidcAdapter;
import makeus.cmc.malmo.adaptor.out.oidc.KakaoOidcAdapter;
import makeus.cmc.malmo.adaptor.out.oidc.KakaoRestApiAdaptor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;
import makeus.cmc.malmo.integration_test.dto_factory.CoupleRequestDtoFactory;
import makeus.cmc.malmo.integration_test.dto_factory.SignInRequestDtoFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class SignInIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    @MockBean
    private AppleOidcAdapter appleOidcAdapter;

    @MockBean
    private KakaoOidcAdapter kakaoOidcAdapter;

    @MockBean
    private KakaoRestApiAdaptor kakaoRestApiAdaptor;

    private String accessToken;

    private MemberEntity member;

    @BeforeEach
    void setup() {
        member = MemberEntity.builder()
                .provider(Provider.KAKAO)
                .providerId("member-provider-id")
                .memberRole(MemberRole.MEMBER)
                .memberState(MemberState.ALIVE)
                .email("testEmail@test.com")
                .nickname("nickname")
                .startLoveDate(LocalDate.of(2023, 10, 1))
                .inviteCodeEntityValue(InviteCodeEntityValue.of("testInviteCode"))
                .build();

        em.persist(member);
        em.flush();
    }

    @Nested
    @DisplayName("카카오 소셜 로그인 테스트")
    class KakaoSignInTest {

        @Test
        @DisplayName("등록된 사용자의 경우 카카오 소셜 로그인에 성공한다")
        void 등록된_사용자의_경우_카카오_소셜_로그인에_성공한다() throws Exception {
            // given
            String idToken = "valid-id-token";
            String accessToken = "valid-access-token";
            given(kakaoOidcAdapter.validateToken(idToken)).willReturn(member.getProviderId());

            // when & then
            mockMvc.perform(post("/login/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    SignInRequestDtoFactory.createKakaoLoginRequestDto(member.getProviderId(), accessToken)
                            )))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.memberState").value("ALIVE"))
                    .andExpect(jsonPath("$.data.grantType").value("Bearer"));
        }

        @Test
        @DisplayName("등록되지 않은 사용자의 경우 카카오 소셜 로그인에 성공한다")
        void 등록되지_않은_사용자의_경우_카카오_소셜_로그인에_성공한다() throws Exception {
            // given
            String providerId = "guest-provider-id";
            String idToken = "valid-id-token";
            String accessToken = "valid-access-token";
            String email = "guest@email.com";
            given(kakaoOidcAdapter.validateToken(idToken)).willReturn(providerId);
            given(kakaoRestApiAdaptor.fetchMemberEmailFromKakao(accessToken)).willReturn(email);

            // when & then
            mockMvc.perform(post("/login/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    SignInRequestDtoFactory.createKakaoLoginRequestDto(idToken, accessToken)
                            )))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.memberState").value("BEFORE_ONBOARDING"))
                    .andExpect(jsonPath("$.data.grantType").value("Bearer"));

            MemberEntity memberEntity = em.createQuery("SELECT m FROM MemberEntity m WHERE m.providerId = :providerId", MemberEntity.class)
                    .setParameter("providerId", providerId)
                    .getSingleResult();

            Assertions.assertThat(memberEntity.getProvider()).isEqualTo(Provider.KAKAO);
            Assertions.assertThat(memberEntity.getProviderId()).isEqualTo(providerId);
            Assertions.assertThat(memberEntity.getEmail()).isEqualTo(email);
            Assertions.assertThat(memberEntity.getMemberState()).isEqualTo(MemberState.BEFORE_ONBOARDING);
        }

        @Test
        @DisplayName("카카오 소셜 로그인 탈퇴한 멤버의 경우 사용자 정보 복구")
        void 카카오_소셜_로그인_탈퇴한_멤버의_경우_사용자_정보_복구() throws Exception {
            // given
            String idToken = "valid-id-token";
            String accessToken = "valid-access-token";
            given(kakaoOidcAdapter.validateToken(idToken)).willReturn(member.getProviderId());

            em.createQuery("UPDATE MemberEntity m SET m.memberState = :state, m.deletedAt = :deletedAt WHERE m.id = :memberId")
                    .setParameter("state", MemberState.DELETED)
                    .setParameter("deletedAt", LocalDateTime.now())
                    .setParameter("memberId", member.getId())
                    .executeUpdate();

            mockMvc.perform(post("/login/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    SignInRequestDtoFactory.createKakaoLoginRequestDto(idToken, accessToken)
                            )))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.memberState").value("ALIVE"))
                    .andExpect(jsonPath("$.data.grantType").value("Bearer"));

            MemberEntity recoveredMember = em.find(MemberEntity.class, member.getId());
            Assertions.assertThat(recoveredMember.getMemberState()).isEqualTo(MemberState.ALIVE);
            Assertions.assertThat(recoveredMember.getDeletedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("애플 소셜 로그인 테스트")
    class AppleSignInTest {

        @Test
        @DisplayName("등록된 사용자의 경우 애플 소셜 로그인에 성공한다")
        void 등록된_사용자의_경우_애플_소셜_로그인에_성공한다() throws Exception {
            // given
        }

        @Test
        @DisplayName("등록되지 않은 사용자의 경우 애플 소셜 로그인에 성공한다")
        void 등록되지_않은_사용자의_경우_애플_소셜_로그인에_성공한다() throws Exception {
            // given
        }

        @Test
        @DisplayName("애플 소셜 로그인 탈퇴한 멤버의 경우 사용자 정보 복구")
        void 애플_소셜_로그인_탈퇴한_멤버의_경우_사용자_정보_복구() throws Exception {
            // given
        }
    }

    @Nested
    @DisplayName("토큰 재발급 테스트")
    class RefreshTokenTest {
        // TODO : 리프레시 토큰을 통한 액세스 토큰 재발급에 성공한다
        // TODO : 만료된 리프레시 토큰을 통한 액세스 토큰 재발급에 실패한다
        // TODO : 유효하지 않은 리프레시 토큰을 통한 액세스 토큰 재발급에 실패한다
        // TODO : 탈퇴한 사용자의 리프레시 토큰을 통한 액세스 토큰 재발급에 실패한다
    }
}
