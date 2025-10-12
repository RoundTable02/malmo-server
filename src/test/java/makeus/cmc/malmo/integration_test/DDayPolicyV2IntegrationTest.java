package makeus.cmc.malmo.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.TermsEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.application.port.out.member.GenerateTokenPort;
import makeus.cmc.malmo.domain.value.state.CoupleState;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;
import makeus.cmc.malmo.domain.value.type.TermsType;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 디데이 정책 V2 통합 테스트
 *
 * 테스트 시나리오:
 * 1. V2 온보딩 API는 startLoveDate 없이 회원가입
 * 2. 커플 연동 시 startLoveDate는 당일로 초기화
 * 3. V2 디데이 변경 API는 커플만 사용 가능
 * 4. 회원 정보 조회 시 커플의 startLoveDate 반환
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class DDayPolicyV2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private GenerateTokenPort generateTokenPort;

    @Nested
    @DisplayName("V2 온보딩 API 테스트")
    class SignUpV2Test {

        private String accessToken;
        private MemberEntity member;
        private TermsEntity terms;

        @BeforeEach
        void setup() {
            // 약관 생성
            terms = TermsEntity.builder()
                    .termsType(TermsType.SERVICE_USAGE)
                    .content("서비스 이용약관")
                    .isRequired(true)
                    .build();
            em.persist(terms);

            // 미가입 상태의 멤버 생성 (OAuth 인증만 완료)
            member = MemberEntity.builder()
                    .provider(Provider.KAKAO)
                    .providerId("testProviderId")
                    .memberRole(MemberRole.MEMBER)
                    .memberState(MemberState.BEFORE_ONBOARDING)
                    .email("test@test.com")
                    .inviteCodeEntityValue(InviteCodeEntityValue.of("TEST1234"))
                    .build();

            em.persist(member);
            em.flush();

            TokenInfo tokenInfo = generateTokenPort.generateToken(member.getId(), member.getMemberRole());
            accessToken = tokenInfo.getAccessToken();
        }

        @Test
        @DisplayName("V2 온보딩 - startLoveDate 없이 회원가입 성공")
        void signUpV2WithoutStartLoveDate() throws Exception {
            // given
            Map<String, Object> requestDto = Map.of(
                    "nickname", "테스트유저",
                    "terms", List.of(
                            Map.of("termsId", terms.getId(), "isAgreed", true)
                    )
            );

            // when & then
            mockMvc.perform(post("/v2/members/onboarding")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            // 회원가입 후 확인
            em.flush();
            em.clear();

            MemberEntity savedMember = em.find(MemberEntity.class, member.getId());
            assertThat(savedMember.getNickname()).isEqualTo("테스트유저");
            assertThat(savedMember.getMemberState()).isEqualTo(MemberState.ALIVE);
            // V2에서는 개인의 startLoveDate가 null이어야 함
            assertThat(savedMember.getStartLoveDate()).isNull();
        }
    }

    @Nested
    @DisplayName("커플 연동 시 startLoveDate 당일 초기화 테스트")
    class CoupleLinkTest {

        private String accessToken;
        private MemberEntity member;
        private MemberEntity partner;

        @BeforeEach
        void setup() {
            // 회원1 생성 (회원가입 완료)
            member = MemberEntity.builder()
                    .provider(Provider.KAKAO)
                    .providerId("testProviderId")
                    .memberRole(MemberRole.MEMBER)
                    .memberState(MemberState.ALIVE)
                    .nickname("테스트유저1")
                    .email("test1@test.com")
                    .inviteCodeEntityValue(InviteCodeEntityValue.of("TEST1234"))
                    .build();

            // 회원2 (파트너) 생성
            partner = MemberEntity.builder()
                    .provider(Provider.KAKAO)
                    .providerId("partnerProviderId")
                    .memberRole(MemberRole.MEMBER)
                    .memberState(MemberState.ALIVE)
                    .nickname("파트너")
                    .email("partner@test.com")
                    .inviteCodeEntityValue(InviteCodeEntityValue.of("PARTNER1"))
                    .build();

            em.persist(member);
            em.persist(partner);
            em.flush();

            TokenInfo tokenInfo = generateTokenPort.generateToken(member.getId(), member.getMemberRole());
            accessToken = tokenInfo.getAccessToken();
        }

        @Test
        @DisplayName("커플 연동 시 startLoveDate는 당일로 초기화")
        void coupleLinkInitializesStartLoveDateToToday() throws Exception {
            // given
            LocalDate today = LocalDate.now();
            Map<String, Object> requestDto = Map.of(
                    "coupleCode", "PARTNER1"
            );

            // when - 멤버가 파트너의 초대코드로 커플 연동
            mockMvc.perform(post("/couples")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            // then
            em.flush();
            em.clear();

            MemberEntity savedMember = em.find(MemberEntity.class, member.getId());
            CoupleEntity couple = em.find(CoupleEntity.class, savedMember.getCoupleEntityId().getValue());

            // 커플의 startLoveDate가 당일인지 확인
            assertThat(couple.getStartLoveDate()).isEqualTo(today);
            assertThat(couple.getCoupleState()).isEqualTo(CoupleState.ALIVE);
        }
    }

    @Nested
    @DisplayName("V2 디데이 변경 API 테스트")
    class UpdateStartLoveDateV2Test {

        private String accessToken;
        private String nonCoupleAccessToken;
        private MemberEntity member;
        private MemberEntity partner;
        private MemberEntity nonCoupleMember;
        private CoupleEntity couple;

        @BeforeEach
        void setup() {
            // 커플 회원1
            member = MemberEntity.builder()
                    .provider(Provider.KAKAO)
                    .providerId("testProviderId")
                    .memberRole(MemberRole.MEMBER)
                    .memberState(MemberState.ALIVE)
                    .nickname("테스트유저1")
                    .email("test1@test.com")
                    .inviteCodeEntityValue(InviteCodeEntityValue.of("TEST1234"))
                    .loveTypeCategory(LoveTypeCategory.STABLE_TYPE)
                    .build();

            // 커플 회원2 (파트너)
            partner = MemberEntity.builder()
                    .provider(Provider.KAKAO)
                    .providerId("partnerProviderId")
                    .memberRole(MemberRole.MEMBER)
                    .memberState(MemberState.ALIVE)
                    .nickname("파트너")
                    .email("partner@test.com")
                    .inviteCodeEntityValue(InviteCodeEntityValue.of("PARTNER1"))
                    .loveTypeCategory(LoveTypeCategory.STABLE_TYPE)
                    .build();

            // 커플이 아닌 회원
            nonCoupleMember = MemberEntity.builder()
                    .provider(Provider.KAKAO)
                    .providerId("nonCoupleProviderId")
                    .memberRole(MemberRole.MEMBER)
                    .memberState(MemberState.ALIVE)
                    .nickname("솔로유저")
                    .email("solo@test.com")
                    .inviteCodeEntityValue(InviteCodeEntityValue.of("SOLO123"))
                    .build();

            em.persist(member);
            em.persist(partner);
            em.persist(nonCoupleMember);
            em.flush();

            // 커플 생성
            couple = CoupleEntity.builder()
                    .startLoveDate(LocalDate.now())
                    .coupleState(CoupleState.ALIVE)
                    .firstMemberId(makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId.of(member.getId()))
                    .secondMemberId(makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId.of(partner.getId()))
                    .build();

            em.persist(couple);
            em.flush();

            // 커플 연결 (새로운 Entity로 생성)
            MemberEntity updatedMember = em.find(MemberEntity.class, member.getId());
            MemberEntity memberWithCouple = MemberEntity.builder()
                    .id(updatedMember.getId())
                    .provider(updatedMember.getProvider())
                    .providerId(updatedMember.getProviderId())
                    .memberRole(updatedMember.getMemberRole())
                    .memberState(updatedMember.getMemberState())
                    .nickname(updatedMember.getNickname())
                    .email(updatedMember.getEmail())
                    .inviteCodeEntityValue(updatedMember.getInviteCodeEntityValue())
                    .loveTypeCategory(updatedMember.getLoveTypeCategory())
                    .coupleEntityId(CoupleEntityId.of(couple.getId()))
                    .build();
            em.merge(memberWithCouple);

            MemberEntity updatedPartner = em.find(MemberEntity.class, partner.getId());
            MemberEntity partnerWithCouple = MemberEntity.builder()
                    .id(updatedPartner.getId())
                    .provider(updatedPartner.getProvider())
                    .providerId(updatedPartner.getProviderId())
                    .memberRole(updatedPartner.getMemberRole())
                    .memberState(updatedPartner.getMemberState())
                    .nickname(updatedPartner.getNickname())
                    .email(updatedPartner.getEmail())
                    .inviteCodeEntityValue(updatedPartner.getInviteCodeEntityValue())
                    .loveTypeCategory(updatedPartner.getLoveTypeCategory())
                    .coupleEntityId(CoupleEntityId.of(couple.getId()))
                    .build();
            em.merge(partnerWithCouple);

            em.flush();
            em.clear();

            TokenInfo tokenInfo = generateTokenPort.generateToken(member.getId(), member.getMemberRole());
            accessToken = tokenInfo.getAccessToken();

            TokenInfo nonCoupleToken = generateTokenPort.generateToken(nonCoupleMember.getId(), nonCoupleMember.getMemberRole());
            nonCoupleAccessToken = nonCoupleToken.getAccessToken();
        }

        @Test
        @DisplayName("V2 디데이 변경 - 커플은 연애 시작일 변경 가능")
        void updateStartLoveDateV2ForCouple() throws Exception {
            // given
            LocalDate newStartDate = LocalDate.of(2024, 1, 1);
            Map<String, Object> requestDto = Map.of(
                    "startLoveDate", newStartDate.toString()
            );

            // when
            mockMvc.perform(patch("/members/v2/start-love-date")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.startLoveDate").value(newStartDate.toString()));

            // then - 커플의 startLoveDate만 변경되었는지 확인
            em.flush();
            em.clear();

            CoupleEntity updatedCouple = em.find(CoupleEntity.class, couple.getId());
            assertThat(updatedCouple.getStartLoveDate()).isEqualTo(newStartDate);

            // 개인의 startLoveDate는 null 유지
            MemberEntity updatedMember = em.find(MemberEntity.class, member.getId());
            assertThat(updatedMember.getStartLoveDate()).isNull();
        }

        @Test
        @DisplayName("V2 디데이 변경 - 커플이 아닌 사용자는 실패")
        void updateStartLoveDateV2ForNonCoupleFails() throws Exception {
            // given
            LocalDate newStartDate = LocalDate.of(2024, 1, 1);
            Map<String, Object> requestDto = Map.of(
                    "startLoveDate", newStartDate.toString()
            );

            // when & then - 커플이 아닌 사용자는 오류 발생
            mockMvc.perform(patch("/members/v2/start-love-date")
                            .header("Authorization", "Bearer " + nonCoupleAccessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().is5xxServerError()); // IllegalStateException 발생
        }
    }

    @Nested
    @DisplayName("회원 정보 조회 시 커플의 startLoveDate 반환 테스트")
    class GetMemberInfoTest {

        private String accessToken;
        private String nonCoupleAccessToken;
        private MemberEntity member;
        private MemberEntity partner;
        private MemberEntity nonCoupleMember;
        private CoupleEntity couple;

        @BeforeEach
        void setup() {
            // 커플 회원1
            member = MemberEntity.builder()
                    .provider(Provider.KAKAO)
                    .providerId("testProviderId")
                    .memberRole(MemberRole.MEMBER)
                    .memberState(MemberState.ALIVE)
                    .nickname("테스트유저1")
                    .email("test1@test.com")
                    .inviteCodeEntityValue(InviteCodeEntityValue.of("TEST1234"))
                    .loveTypeCategory(LoveTypeCategory.STABLE_TYPE)
                    .build();

            // 커플 회원2 (파트너)
            partner = MemberEntity.builder()
                    .provider(Provider.KAKAO)
                    .providerId("partnerProviderId")
                    .memberRole(MemberRole.MEMBER)
                    .memberState(MemberState.ALIVE)
                    .nickname("파트너")
                    .email("partner@test.com")
                    .inviteCodeEntityValue(InviteCodeEntityValue.of("PARTNER1"))
                    .loveTypeCategory(LoveTypeCategory.STABLE_TYPE)
                    .build();

            // 커플이 아닌 회원
            nonCoupleMember = MemberEntity.builder()
                    .provider(Provider.KAKAO)
                    .providerId("nonCoupleProviderId")
                    .memberRole(MemberRole.MEMBER)
                    .memberState(MemberState.ALIVE)
                    .nickname("솔로유저")
                    .email("solo@test.com")
                    .inviteCodeEntityValue(InviteCodeEntityValue.of("SOLO123"))
                    .build();

            em.persist(member);
            em.persist(partner);
            em.persist(nonCoupleMember);
            em.flush();

            // 커플 생성 (특정 날짜로 설정)
            LocalDate coupleStartDate = LocalDate.of(2024, 5, 1);
            couple = CoupleEntity.builder()
                    .startLoveDate(coupleStartDate)
                    .coupleState(CoupleState.ALIVE)
                    .firstMemberId(makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId.of(member.getId()))
                    .secondMemberId(makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId.of(partner.getId()))
                    .build();

            em.persist(couple);
            em.flush();

            // 커플 연결
            MemberEntity updatedMember = em.find(MemberEntity.class, member.getId());
            MemberEntity memberWithCouple = MemberEntity.builder()
                    .id(updatedMember.getId())
                    .provider(updatedMember.getProvider())
                    .providerId(updatedMember.getProviderId())
                    .memberRole(updatedMember.getMemberRole())
                    .memberState(updatedMember.getMemberState())
                    .nickname(updatedMember.getNickname())
                    .email(updatedMember.getEmail())
                    .inviteCodeEntityValue(updatedMember.getInviteCodeEntityValue())
                    .loveTypeCategory(updatedMember.getLoveTypeCategory())
                    .coupleEntityId(CoupleEntityId.of(couple.getId()))
                    .build();
            em.merge(memberWithCouple);

            em.flush();
            em.clear();

            TokenInfo tokenInfo = generateTokenPort.generateToken(member.getId(), member.getMemberRole());
            accessToken = tokenInfo.getAccessToken();

            TokenInfo nonCoupleToken = generateTokenPort.generateToken(nonCoupleMember.getId(), nonCoupleMember.getMemberRole());
            nonCoupleAccessToken = nonCoupleToken.getAccessToken();
        }

        @Test
        @DisplayName("회원 정보 조회 - 커플의 startLoveDate 반환")
        void getMemberInfoReturnsCoupleStartLoveDate() throws Exception {
            // given
            LocalDate coupleStartDate = LocalDate.of(2024, 5, 1);

            // when & then
            mockMvc.perform(get("/members")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.startLoveDate").value(coupleStartDate.toString()));
        }

        @Test
        @DisplayName("회원 정보 조회 - 커플이 아닌 경우 startLoveDate 없음")
        void getMemberInfoReturnsNullForNonCouple() throws Exception {
            // when & then
            mockMvc.perform(get("/members")
                            .header("Authorization", "Bearer " + nonCoupleAccessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.startLoveDate").doesNotExist());
        }
    }
}
