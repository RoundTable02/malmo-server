package makeus.cmc.malmo.integration_test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import makeus.cmc.malmo.adaptor.in.web.controller.SignUpController;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.CoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.terms.MemberTermsAgreementEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.application.port.out.GenerateTokenPort;
import makeus.cmc.malmo.domain.value.state.*;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;
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
import java.time.LocalDateTime;
import java.util.List;

import static makeus.cmc.malmo.adaptor.in.exception.ErrorCode.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
            MemberEntity partner = createAndSavePartner();

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

        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public static class ResponseDto <T> {
            String requestId;
            boolean success;
            String message;
            T data;
        }

        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public static class MemberResponseDto {
            MemberState memberState;
            Provider provider;
            LocalDate startLoveDate;

            LoveTypeCategory loveTypeCategory;

            int totalChatRoomCount;
            int totalCoupleQuestionCount;

            float avoidanceRate;
            float anxietyRate;
            String nickname;
            String email;
        }

        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public static class PartnerResponseDto {
            private MemberState memberState;
            private LoveTypeCategory loveTypeCategory;
            private float avoidanceRate;
            private float anxietyRate;
            private String nickname;
        }

        void assertMemberInfo(MemberResponseDto memberResponse, MemberEntity member, LocalDate startLoveDate, int coupleQuestionCount, int totalChatRoomCount) {
            Assertions.assertThat(memberResponse.memberState).isEqualTo(member.getMemberState());
            Assertions.assertThat(memberResponse.provider).isEqualTo(member.getProvider());
            Assertions.assertThat(memberResponse.startLoveDate).isEqualTo(startLoveDate);
            Assertions.assertThat(memberResponse.loveTypeCategory).isEqualTo(member.getLoveTypeCategory());
            Assertions.assertThat(memberResponse.totalCoupleQuestionCount).isEqualTo(coupleQuestionCount);
            Assertions.assertThat(memberResponse.totalChatRoomCount).isEqualTo(totalChatRoomCount);
            Assertions.assertThat(memberResponse.avoidanceRate).isEqualTo(member.getAvoidanceRate());
            Assertions.assertThat(memberResponse.anxietyRate).isEqualTo(member.getAnxietyRate());
            Assertions.assertThat(memberResponse.nickname).isEqualTo(member.getNickname());
            Assertions.assertThat(memberResponse.email).isEqualTo(member.getEmail());
        }

        @Test
        @DisplayName("멤버 정보 조회 성공")
        void 멤버_정보_조회_성공() throws Exception {
            // when
            MvcResult mvcResult = mockMvc.perform(get("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            ResponseDto<MemberResponseDto> responseDto = objectMapper.readValue(
                    responseContent,
                    new TypeReference<>() {}
            );

            // 멤버 정보가 정상적으로 조회되었는지 검증
            assertMemberInfo(responseDto.data, member, member.getStartLoveDate(), 0, 0);
        }

        @Test
        @DisplayName("커플 멤버의 경우 멤버 정보 조회 성공")
        void 커플_멤버_정보_조회_성공() throws Exception {
            // given
            MemberEntity partner = createAndSavePartner();

            mockMvc.perform(post("/couples")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    CoupleRequestDtoFactory.createCoupleLinkRequestDto(partner.getInviteCodeEntityValue().getValue())
                            )))
                    .andExpect(status().isOk());

            // when
            MvcResult mvcResult = mockMvc.perform(get("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            ResponseDto<MemberResponseDto> responseDto = objectMapper.readValue(
                    responseContent,
                    new TypeReference<>() {}
            );

            // 커플인 경우 커플의 연애 시작 날짜(초대코드 주인의 날짜)로 조회
            assertMemberInfo(responseDto.data, member, partner.getStartLoveDate(), 0, 0);
        }

        @Test
        @DisplayName("완료된 채팅방 수와 커플 질문 수 조회 성공")
        void 완료된_채팅방_수와_커플_질문_수_조회_성공() throws Exception {
            // given
            MemberEntity partner = createAndSavePartner();

            MvcResult coupleResult = mockMvc.perform(post("/couples")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    CoupleRequestDtoFactory.createCoupleLinkRequestDto(partner.getInviteCodeEntityValue().getValue())
                            )))
                    .andExpect(status().isOk())
                    .andReturn();

            String coupleContent = coupleResult.getResponse().getContentAsString();
            Integer coupleId = JsonPath.read(coupleContent, "$.data.coupleId");

            // 커플 완료 질문 3개 생성
            // 현재 질문 종료
            em.createQuery("update CoupleQuestionEntity c SET c.coupleQuestionState = 'OUTDATED' where c.coupleEntityId.value = :coupleId")
                    .setParameter("coupleId", coupleId)
                    .executeUpdate();
            em.flush();
            QuestionEntity questionEntity = em.find(QuestionEntity.class, 1L);
            createAndSaveCoupleQuestion(questionEntity, coupleId);
            createAndSaveCoupleQuestion(questionEntity, coupleId);

            // 멤버 완료된 채팅방 3개 생성
            createAndSaveChatRoom();
            createAndSaveChatRoom();
            createAndSaveChatRoom();

            // when
            MvcResult mvcResult = mockMvc.perform(get("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            ResponseDto<MemberResponseDto> responseDto = objectMapper.readValue(
                    responseContent,
                    new TypeReference<>() {}
            );

            assertMemberInfo(responseDto.data, member, partner.getStartLoveDate(), 3, 3);
        }


        @Test
        @DisplayName("탈퇴한 멤버의 경우 멤버 정보 조회 실패")
        void 탈퇴한_멤버_정보_조회_실패() throws Exception {
            // given
            mockMvc.perform(delete("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // when & then
            mockMvc.perform(get("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(NO_SUCH_MEMBER.getMessage()))
                    .andExpect(jsonPath("code").value(NO_SUCH_MEMBER.getCode()));
        }

        @Test
        @DisplayName("파트너 멤버 정보 조회 성공")
        void 파트너_멤버_정보_조회_성공() throws Exception {
            // given
            MemberEntity partner = createAndSavePartner();

            mockMvc.perform(post("/couples")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    CoupleRequestDtoFactory.createCoupleLinkRequestDto(partner.getInviteCodeEntityValue().getValue())
                            )))
                    .andExpect(status().isOk());

            // when
            MvcResult mvcResult = mockMvc.perform(get("/members/partner")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            ResponseDto<PartnerResponseDto> responseDto = objectMapper.readValue(
                    responseContent,
                    new TypeReference<>() {}
            );

            // 파트너 멤버 정보가 정상적으로 조회되었는지 검증
            PartnerResponseDto partnerDto = responseDto.data;
            Assertions.assertThat(partnerDto.memberState).isEqualTo(partner.getMemberState());
            Assertions.assertThat(partnerDto.loveTypeCategory).isEqualTo(partner.getLoveTypeCategory());
            Assertions.assertThat(partnerDto.avoidanceRate).isEqualTo(partner.getAvoidanceRate());
            Assertions.assertThat(partnerDto.anxietyRate).isEqualTo(partner.getAnxietyRate());
            Assertions.assertThat(partnerDto.nickname).isEqualTo(partner.getNickname());
        }

        @Test
        @DisplayName("파트너 멤버 정보 조회 실패 - 커플이 아닌 경우")
        void 파트너_멤버_정보_조회_실패_커플이_아닌_경우() throws Exception {
            // when & then
            mockMvc.perform(get("/members/partner")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("message").value(NOT_COUPLE_MEMBER.getMessage()))
                    .andExpect(jsonPath("code").value(NOT_COUPLE_MEMBER.getCode()));
        }

        @Test
        @DisplayName("파트너 멤버 정보 조회 실패 - 탈퇴한 멤버인 경우")
        void 파트너_멤버_정보_조회_실패_탈퇴한_멤버인_경우() throws Exception {
            // given
            MemberEntity partner = createAndSavePartner();

            mockMvc.perform(post("/couples")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    CoupleRequestDtoFactory.createCoupleLinkRequestDto(partner.getInviteCodeEntityValue().getValue())
                            )))
                    .andExpect(status().isOk());

            // 탈퇴 처리
            mockMvc.perform(delete("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // when & then
            mockMvc.perform(get("/members/partner")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("message").value(NOT_COUPLE_MEMBER.getMessage()))
                    .andExpect(jsonPath("code").value(NOT_COUPLE_MEMBER.getCode()));
        }

        @Test
        @DisplayName("초대코드 조회 성공")
        void 초대코드_조회_성공() throws Exception {
            // when
            MvcResult mvcResult = mockMvc.perform(get("/members/invite-code")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            String coupleCode = JsonPath.read(responseContent, "$.data.coupleCode");

            Assertions.assertThat(coupleCode).isEqualTo(member.getInviteCodeEntityValue().getValue());
        }

        @Test
        @DisplayName("초대코드 조회 실패 - 탈퇴한 멤버인 경우")
        void 초대코드_조회_실패_탈퇴한_멤버인_경우() throws Exception {
            // given
            mockMvc.perform(delete("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // when & then
            mockMvc.perform(get("/members/invite-code")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(NO_SUCH_MEMBER.getMessage()))
                    .andExpect(jsonPath("code").value(NO_SUCH_MEMBER.getCode()));
        }
    }

    @Nested
    @DisplayName("멤버 정보 수정 검증")
    class MemberInfoUpdateFeature {
        @Test
        @DisplayName("멤버 정보 수정 성공")
        void 멤버_정보_수정_성공() throws Exception {
            // given
            String newNickname = "newName";
            // when
            MvcResult mvcResult = mockMvc.perform(patch("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createUpdateMemberRequestDto(newNickname)
                            )))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            String response = JsonPath.read(responseContent, "$.data.nickname");

            MemberEntity savedMember = em.createQuery("SELECT m FROM MemberEntity m WHERE m.email = :email", MemberEntity.class)
                    .setParameter("email", member.getEmail())
                    .getSingleResult();

            Assertions.assertThat(response).isEqualTo(newNickname);
            Assertions.assertThat(savedMember.getNickname()).isEqualTo(newNickname);
        }

        @Test
        @DisplayName("멤버 정보 수정 실패 - 탈퇴한 멤버인 경우")
        void 멤버_정보_수정_실패_탈퇴한_멤버인_경우() throws Exception {
            // given
            mockMvc.perform(delete("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // when & then
            mockMvc.perform(patch("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createUpdateMemberRequestDto("newName")
                            )))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(NO_SUCH_MEMBER.getMessage()))
                    .andExpect(jsonPath("code").value(NO_SUCH_MEMBER.getCode()));
        }

        @Test
        @DisplayName("멤버 정보 수정 실패 - 특수 기호가 포함된 경우")
        void 멤버_정보_수정_실패_닉네임_규격에_맞지_않는_경우() throws Exception {
            // given
            String invalidNickname = "invalid!@#";

            // when & then
            mockMvc.perform(patch("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createUpdateMemberRequestDto(invalidNickname)
                            )))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(BAD_REQUEST.getMessage()))
                    .andExpect(jsonPath("code").value(BAD_REQUEST.getCode()));
        }

        @Test
        @DisplayName("멤버 정보 수정 실패 - 길이가 초과된 경우")
        void 멤버_정보_수정_실패_닉네임_길이가_초과된_경우() throws Exception {
            // given
            String invalidNickname = "invalid1234"; // 11자

            // when & then
            mockMvc.perform(patch("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createUpdateMemberRequestDto(invalidNickname)
                            )))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(BAD_REQUEST.getMessage()))
                    .andExpect(jsonPath("code").value(BAD_REQUEST.getCode()));
        }

        @Test
        @DisplayName("디데이 수정 성공")
        void 디데이_수정_성공() throws Exception {
            // given
            LocalDate newDday = LocalDate.of(2024, 1, 1);

            // when
            mockMvc.perform(patch("/members/start-love-date")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createUpdateStartLoveDateRequestDto(newDday)
                            )))
                    .andExpect(status().isOk());

            // then
            MemberEntity savedMember = em.createQuery("SELECT m FROM MemberEntity m WHERE m.email = :email", MemberEntity.class)
                    .setParameter("email", member.getEmail())
                    .getSingleResult();

            Assertions.assertThat(savedMember.getStartLoveDate()).isEqualTo(newDday);
        }

        @Test
        @DisplayName("커플 멤버인 경우 디데이 수정 성공")
        void 커플_멤버인_경우_디데이_수정_성공() throws Exception {
            // given
            MemberEntity partner = createAndSavePartner();
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

            LocalDate newDday = LocalDate.of(2025, 1, 1);
            // when
            mockMvc.perform(patch("/members/start-love-date")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createUpdateStartLoveDateRequestDto(newDday)
                            )))
                    .andExpect(status().isOk());

            // then
            MemberEntity savedMember = em.createQuery("SELECT m FROM MemberEntity m WHERE m.email = :email", MemberEntity.class)
                    .setParameter("email", member.getEmail())
                    .getSingleResult();
            Assertions.assertThat(savedMember.getStartLoveDate()).isEqualTo(newDday);

            // 커플의 디데이도 함께 수정되어야 함
            CoupleEntity couple = em.createQuery("SELECT c FROM CoupleEntity c WHERE c.id = :coupleId", CoupleEntity.class)
                    .setParameter("coupleId", coupleId)
                    .getSingleResult();
            Assertions.assertThat(couple.getStartLoveDate()).isEqualTo(newDday);
        }

        @Test
        @DisplayName("디데이 수정 실패 - 탈퇴한 멤버인 경우")
        void 디데이_수정_실패_탈퇴한_멤버인_경우() throws Exception {
            // given
            mockMvc.perform(delete("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            LocalDate newDday = LocalDate.of(2024, 1, 1);

            // when & then
            mockMvc.perform(patch("/members/start-love-date")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createUpdateStartLoveDateRequestDto(newDday)
                            )))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(NO_SUCH_MEMBER.getMessage()))
                    .andExpect(jsonPath("code").value(NO_SUCH_MEMBER.getCode()));
        }

        @Test
        @DisplayName("디데이 수정 실패 - 디데이가 오늘보다 이후인 경우")
        void 디데이_수정_실패_디데이가_오늘보다_이후인_경우() throws Exception {
            // given
            LocalDate futureDday = LocalDate.now().plusDays(1);
            // when & then
            mockMvc.perform(patch("/members/start-love-date")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createUpdateStartLoveDateRequestDto(futureDday)
                            )))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(BAD_REQUEST.getMessage()))
                    .andExpect(jsonPath("code").value(BAD_REQUEST.getCode()));
        }

        @Test
        @DisplayName("애착 유형 등록 성공 - 회피형")
        void 애착_유형_등록_성공_회피형() throws Exception {
            // given
            int[] scores = {5, 1, 1, 5, 1, 1, 1, 5, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 5, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 1, 1, 5, 1};

            // when
            mockMvc.perform(post("/members/love-type")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createRegisterLoveTypeRequestDto(scores)
                            )))
                    .andExpect(status().isOk());

            // then
            MemberEntity updatedMember = em.createQuery("SELECT m FROM MemberEntity m WHERE m.email = :email", MemberEntity.class)
                    .setParameter("email", member.getEmail())
                    .getSingleResult();
            Assertions.assertThat(updatedMember.getLoveTypeCategory()).isEqualTo(LoveTypeCategory.AVOIDANCE_TYPE);
            Assertions.assertThat(updatedMember.getAvoidanceRate()).isEqualTo(5.00f);
            Assertions.assertThat(updatedMember.getAnxietyRate()).isEqualTo(1.00f);
        }

        @Test
        @DisplayName("애착 유형 등록 성공 - 불안형")
        void 애착_유형_등록_성공_불안형() throws Exception {
            // given
            int[] scores = {1, 5, 5, 1, 5, 5, 5, 1, 5, 5, 1, 5, 5, 5, 5, 1, 5, 5, 1, 5, 5, 5, 1, 5, 5, 5, 5, 1, 5, 5, 5, 5, 5, 5, 1, 5};

            // when
            mockMvc.perform(post("/members/love-type")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createRegisterLoveTypeRequestDto(scores)
                            )))
                    .andExpect(status().isOk());

            // then
            MemberEntity updatedMember = em.createQuery("SELECT m FROM MemberEntity m WHERE m.email = :email", MemberEntity.class)
                    .setParameter("email", member.getEmail())
                    .getSingleResult();
            Assertions.assertThat(updatedMember.getLoveTypeCategory()).isEqualTo(LoveTypeCategory.ANXIETY_TYPE);
            Assertions.assertThat(updatedMember.getAvoidanceRate()).isEqualTo(1.00f);
            Assertions.assertThat(updatedMember.getAnxietyRate()).isEqualTo(5.00f);
        }

        @Test
        @DisplayName("애착 유형 등록 성공 - 혼란형")
        void 애착_유형_등록_성공_혼란형() throws Exception {
            // given
            int[] scores = {5, 5, 1, 5, 1, 5, 1, 1, 1, 5, 5, 5, 5, 5, 5, 5, 5, 1, 5, 5, 5, 5, 1, 5, 5, 5, 1, 5, 1, 1, 5, 1, 1, 1, 5, 5};

            // when
            mockMvc.perform(post("/members/love-type")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createRegisterLoveTypeRequestDto(scores)
                            )))
                    .andExpect(status().isOk());

            // then
            MemberEntity updatedMember = em.createQuery("SELECT m FROM MemberEntity m WHERE m.email = :email", MemberEntity.class)
                    .setParameter("email", member.getEmail())
                    .getSingleResult();
            Assertions.assertThat(updatedMember.getLoveTypeCategory()).isEqualTo(LoveTypeCategory.CONFUSION_TYPE);
            Assertions.assertThat(updatedMember.getAvoidanceRate()).isEqualTo(5.00f);
            Assertions.assertThat(updatedMember.getAnxietyRate()).isEqualTo(5.00f);
        }

        @Test
        @DisplayName("애착 유형 등록 성공 - 안정형")
        void 애착_유형_등록_성공_안정형() throws Exception {
            // given
            int[] scores = {1, 1, 5, 1, 5, 1, 5, 5, 5, 1, 1, 1, 1, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 5, 1, 5, 5, 1, 5, 5, 5, 1, 1};

            // when
            mockMvc.perform(post("/members/love-type")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createRegisterLoveTypeRequestDto(scores)
                            )))
                    .andExpect(status().isOk());

            // then
            MemberEntity updatedMember = em.createQuery("SELECT m FROM MemberEntity m WHERE m.email = :email", MemberEntity.class)
                    .setParameter("email", member.getEmail())
                    .getSingleResult();
            Assertions.assertThat(updatedMember.getLoveTypeCategory()).isEqualTo(LoveTypeCategory.STABLE_TYPE);
            Assertions.assertThat(updatedMember.getAvoidanceRate()).isEqualTo(1.00f);
            Assertions.assertThat(updatedMember.getAnxietyRate()).isEqualTo(1.00f);
        }

        @Test
        @DisplayName("애착 유형 등록 실패 - 탈퇴한 멤버인 경우")
        void 애착_유형_등록_실패_탈퇴한_멤버인_경우() throws Exception {
            // given
            mockMvc.perform(delete("/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            int[] scores = {1, 1, 5, 1, 5, 1, 5, 5, 5, 1, 1, 1, 1, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 5, 1, 5, 5, 1, 5, 5, 5, 1, 1};
            // when & then
            mockMvc.perform(post("/members/love-type")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createRegisterLoveTypeRequestDto(scores)
                            )))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(NO_SUCH_MEMBER.getMessage()))
                    .andExpect(jsonPath("code").value(NO_SUCH_MEMBER.getCode()));
        }

        @Test
        @DisplayName("애착 유형 등록 실패 - 점수가 0점인 경우")
        void 애착_유형_등록_실패_점수가_0점인_경우() throws Exception {
            // given
            int[] scores = {1, 1, 5, 1, 0, 1, 5, 5, 5, 1, 1, 1, 1, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 5, 1, 5, 5, 1, 5, 5, 5, 1, 1};
            // when & then
            mockMvc.perform(post("/members/love-type")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createRegisterLoveTypeRequestDto(scores)
                            )))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(BAD_REQUEST.getMessage()))
                    .andExpect(jsonPath("code").value(BAD_REQUEST.getCode()));
        }

        @Test
        @DisplayName("애착 유형 등록 실패 - 점수가 6점인 경우")
        void 애착_유형_등록_실패_점수가_6점인_경우() throws Exception {
            // given
            int[] scores = {1, 1, 5, 1, 6, 1, 5, 5, 5, 1, 1, 1, 1, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 5, 1, 5, 5, 1, 5, 5, 5, 1, 1};
            // when & then
            mockMvc.perform(post("/members/love-type")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createRegisterLoveTypeRequestDto(scores)
                            )))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(BAD_REQUEST.getMessage()))
                    .andExpect(jsonPath("code").value(BAD_REQUEST.getCode()));
        }

        @Test
        @DisplayName("애착 유형 등록 실패 - 존재하지 않는 질문인 경우")
        void 애착_유형_등록_실패_존재하지_않는_질문인_경우() throws Exception {
            // given
            // 36개의 질문에 대한 점수 배열이 필요하지만, 37번에 대한 답변을 했다고 가정
            int[] scores = {1, 1, 5, 1, 1, 1, 5, 5, 5, 1, 1, 1, 1, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 5, 1, 5, 5, 1, 5, 5, 5, 1, 1, 4};
            // when & then
            mockMvc.perform(post("/members/love-type")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    MemberRequestDtoFactory.createRegisterLoveTypeRequestDto(scores)
                            )))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(NO_SUCH_LOVE_TYPE_QUESTION.getMessage()))
                    .andExpect(jsonPath("code").value(NO_SUCH_LOVE_TYPE_QUESTION.getCode()));
        }
    }


    private MemberEntity createAndSavePartner() {
        MemberEntity partner = MemberEntity.builder()
                .provider(Provider.KAKAO)
                .providerId("partnerProviderId")
                .memberRole(MemberRole.MEMBER)
                .memberState(MemberState.ALIVE)
                .nickname("pnickname")
                .loveTypeCategory(LoveTypeCategory.ANXIETY_TYPE)
                .anxietyRate(2.77f)
                .avoidanceRate(1.66f)
                .startLoveDate(LocalDate.of(2024, 1, 1))
                .email("testEmail2@test.com")
                .inviteCodeEntityValue(InviteCodeEntityValue.of("invite2"))
                .build();

        em.persist(partner);
        em.flush();
        return partner;
    }

    private CoupleQuestionEntity createAndSaveCoupleQuestion(QuestionEntity questionEntity, Integer coupleId) {
        CoupleQuestionEntity coupleQuestion = CoupleQuestionEntity.builder()
                .question(questionEntity)
                .coupleEntityId(CoupleEntityId.of(Long.valueOf(coupleId)))
                .coupleQuestionState(CoupleQuestionState.OUTDATED)
                .bothAnsweredAt(LocalDateTime.now())
                .build();
        em.persist(coupleQuestion);
        em.flush();

        return coupleQuestion;
    }

    private ChatRoomEntity createAndSaveChatRoom() {
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .memberEntityId(MemberEntityId.of(member.getId()))
                .chatRoomState(ChatRoomState.COMPLETED)
                .level(1)
                .lastMessageSentTime(LocalDateTime.now())
                .totalSummary("테스트 요약")
                .situationKeyword("테스트 상황 키워드")
                .solutionKeyword("테스트 해결 키워드")
                .build();

        em.persist(chatRoom);
        em.flush();
        return chatRoom;
    }

}
