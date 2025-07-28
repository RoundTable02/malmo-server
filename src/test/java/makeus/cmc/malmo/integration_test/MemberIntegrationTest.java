package makeus.cmc.malmo.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import makeus.cmc.malmo.adaptor.in.web.controller.SignUpController;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.MemberTermsAgreementEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.application.port.out.GenerateTokenPort;
import makeus.cmc.malmo.domain.value.state.CoupleMemberState;
import makeus.cmc.malmo.domain.value.state.CoupleState;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;
import makeus.cmc.malmo.integration_test.dto_factory.CoupleRequestDtoFactory;
import makeus.cmc.malmo.integration_test.dto_factory.MemberRequestDtoFactory;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Nested
    @DisplayName("멤버 탈퇴 검증")
    class MemberDeleteFeature {
        @Test
        @DisplayName("정상적인 요청의 경우 멤버 탈퇴가 성공한다")
        void 멤버_탈퇴_성공() throws Exception {
            // when
            mockMvc.perform(delete("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // then
            MemberEntity deletedMember = em.find(MemberEntity.class, member.getId());
            Assertions.assertThat(deletedMember.getMemberState()).isEqualTo(MemberState.DELETED);
        }

        @Test
        @DisplayName("커플인 멤버의 경우 멤버 탈퇴 시 커플 상태가 DELETED로 변경된다")
        void 멤버_탈퇴_커플_상태_변경() throws Exception {
            // given
            MemberEntity partner = MemberEntity.builder()
                    .provider(Provider.KAKAO)
                    .providerId("partnerProviderId")
                    .memberRole(MemberRole.MEMBER)
                    .memberState(MemberState.ALIVE)
                    .email("testEmail2@test.com")
                    .inviteCodeEntityValue(InviteCodeEntityValue.of("invite2"))
                    .build();

            em.persist(partner);
            em.flush();

            MvcResult mvcResult = mockMvc.perform(post("/couples")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    CoupleRequestDtoFactory.createCoupleLinkRequestDto(partner.getInviteCodeEntityValue().getValue())
                            )))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseContent = mvcResult.getResponse().getContentAsString();
            Integer coupleId = JsonPath.read(responseContent, "$.data.coupleId");

            // when
            mockMvc.perform(delete("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // then
            MemberEntity deletedMember = em.find(MemberEntity.class, member.getId());
            Assertions.assertThat(deletedMember.getMemberState()).isEqualTo(MemberState.DELETED);

            CoupleEntity couple = em.createQuery("SELECT c FROM CoupleEntity c WHERE c.id = :coupleId", CoupleEntity.class)
                    .setParameter("coupleId", coupleId)
                    .getSingleResult();
            Assertions.assertThat(couple.getCoupleState()).isEqualTo(CoupleState.DELETED);
            Assertions.assertThat(couple.getCoupleMembers())
                    .extracting(CoupleMemberEntity::getCoupleMemberState)
                    .containsExactlyInAnyOrder(CoupleMemberState.DELETED, CoupleMemberState.DELETED);
        }
    }

    @Nested
    @DisplayName("멤버 정보 조회 검증")
    class MemberInfoFeature {
        // TODO : 멤버 정보 조회 성공
        // TODO : 탈퇴한 멤버 정보 조회 실패

        // TODO : 파트너 멤버 정보 조회 성공
        // TODO : 파트너 멤버 정보 조회 실패 (커플이 아닌 경우)
        // TODO : 파트너 멤버 정보 조회 실패 (탈퇴한 멤버인 경우)

        // TODO : 초대코드 조회 성공
        // TODO : 초대코드 조회 실패 (탈퇴한 멤버인 경우)
    }

    @Nested
    @DisplayName("멤버 정보 수정 검증")
    class MemberInfoUpdateFeature {
        // TODO : 멤버 정보 수정 성공
        // TODO : 멤버 정보 수정 실패 (탈퇴한 멤버인 경우)
        // TODO : 멤버 정보 수정 실패 (닉네임 규격에 맞지 않는 경우)

        // TODO : 디데이 수정 성공
        // TODO : 디데이 수정 실패 (탈퇴한 멤버인 경우)
        // TODO : 디데이 수정 실패 (디데이가 오늘보다 이전인 경우)

        // TODO : 애착 유형 등록 성공 (안정형)
        // TODO : 애착 유형 등록 성공 (회피형)
        // TODO : 애착 유형 등록 성공 (불안형)
        // TODO : 애착 유형 등록 성공 (혼란형)
        // TODO : 애착 유형 등록 실패 (탈퇴한 멤버인 경우)
        // TODO : 애착 유형 등록 실패 (점수가 0점인 경우)
        // TODO : 애착 유형 등록 실패 (점수가 6점인 경우)
    }

}
