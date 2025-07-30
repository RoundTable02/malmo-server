package makeus.cmc.malmo.integration_test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleMemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.CoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.MemberAnswerEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.QuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.TempCoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleMemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleQuestionEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.application.port.out.GenerateTokenPort;
import makeus.cmc.malmo.domain.model.question.CoupleQuestion;
import makeus.cmc.malmo.domain.model.question.TempCoupleQuestion;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;
import makeus.cmc.malmo.domain.value.state.MemberAnswerState;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;
import makeus.cmc.malmo.integration_test.dto_factory.CoupleRequestDtoFactory;
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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static makeus.cmc.malmo.adaptor.in.exception.ErrorCode.*;
import static makeus.cmc.malmo.domain.service.CoupleQuestionDomainService.FIRST_QUESTION_LEVEL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class CoupleQuestionIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private GenerateTokenPort generateTokenPort;

    private String accessToken;

    private String partnerAccessToken;

    private MemberEntity member;

    private MemberEntity partner;

    private MemberEntity other;

    @BeforeEach
    void setup() {
        member = MemberEntity.builder()
                .provider(Provider.KAKAO)
                .providerId("testProviderId")
                .memberRole(MemberRole.MEMBER)
                .memberState(MemberState.ALIVE)
                .email("testEmail@test.com")
                .inviteCodeEntityValue(InviteCodeEntityValue.of("invite1"))
                .build();

        partner = MemberEntity.builder()
                .provider(Provider.KAKAO)
                .providerId("partnerProviderId")
                .memberRole(MemberRole.MEMBER)
                .memberState(MemberState.ALIVE)
                .email("testEmail2@test.com")
                .inviteCodeEntityValue(InviteCodeEntityValue.of("invite2"))
                .build();

        other = MemberEntity.builder()
                .provider(Provider.KAKAO)
                .providerId("partnerProviderId")
                .memberRole(MemberRole.MEMBER)
                .memberState(MemberState.ALIVE)
                .email("testEmail3@test.com")
                .inviteCodeEntityValue(InviteCodeEntityValue.of("invite3"))
                .build();

        em.persist(member);
        em.persist(partner);
        em.persist(other);
        em.flush();

        TokenInfo tokenInfo = generateTokenPort.generateToken(member.getId(), member.getMemberRole());
        TokenInfo partnerTokenInfo = generateTokenPort.generateToken(partner.getId(), partner.getMemberRole());

        accessToken = tokenInfo.getAccessToken();
        partnerAccessToken = partnerTokenInfo.getAccessToken();
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class ResponseDto <T> {
        String requestId;
        boolean success;
        String message;
        T data;
    }

    @Nested
    @DisplayName("오늘의 질문 조회 기능 검증")
    class CoupleTodayQuestionFeature {

        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public static class GetQuestionResponse {
            Long coupleQuestionId;
            String title;
            String content;
            int level;
            boolean meAnswered;
            boolean partnerAnswered;
            LocalDateTime createdAt;
        }

        @Test
        @DisplayName("커플이 아닌 멤버 오늘의 질문 조회 성공")
        void 커플_x_멤버_오늘의_질문_조회_성공() throws Exception {
            // when
            MvcResult mvcResult = mockMvc.perform(get("/questions/today")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(new MediaType("application", "json", StandardCharsets.UTF_8)))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            ResponseDto<GetQuestionResponse> responseDto = objectMapper.readValue(
                    responseContent,
                    new TypeReference<>() {}
            );

            GetQuestionResponse data = responseDto.data;
            QuestionEntity question = em.createQuery("SELECT q FROM QuestionEntity q WHERE q.level = :level", QuestionEntity.class)
                    .setParameter("level", FIRST_QUESTION_LEVEL)
                    .getSingleResult();

            TempCoupleQuestionEntity tempQuestion = em.createQuery("SELECT tq FROM TempCoupleQuestionEntity tq WHERE tq.memberId.value = :memberId", TempCoupleQuestionEntity.class)
                    .setParameter("memberId", member.getId())
                    .getSingleResult();

            Assertions.assertThat(data.content).isEqualTo(question.getContent());
            Assertions.assertThat(data.title).isEqualTo(question.getTitle());
            Assertions.assertThat(data.level).isEqualTo(FIRST_QUESTION_LEVEL);
            Assertions.assertThat(data.meAnswered).isFalse();
            Assertions.assertThat(data.partnerAnswered).isFalse();
            Assertions.assertThat(data.createdAt).isNotNull();

            Assertions.assertThat(tempQuestion.getAnswer()).isNull();
            Assertions.assertThat(tempQuestion.getQuestion().getId()).isEqualTo(question.getId());
            Assertions.assertThat(tempQuestion.getCoupleQuestionState()).isEqualTo(CoupleQuestionState.ALIVE);
        }

        @Test
        @DisplayName("커플인 멤버 오늘의 질문 조회 성공")
        void 커플_멤버_오늘의_질문_조회_성공() throws Exception {
            // given
            mockMvc.perform(post("/couples")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    CoupleRequestDtoFactory.createCoupleLinkRequestDto(partner.getInviteCodeEntityValue().getValue())
                            )))
                    .andExpect(status().isOk());

            // when
            MvcResult mvcResult = mockMvc.perform(get("/questions/today")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(new MediaType("application", "json", StandardCharsets.UTF_8)))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            ResponseDto<GetQuestionResponse> responseDto = objectMapper.readValue(
                    responseContent,
                    new TypeReference<>() {}
            );

            GetQuestionResponse data = responseDto.data;
            QuestionEntity question = em.createQuery("SELECT q FROM QuestionEntity q WHERE q.level = :level", QuestionEntity.class)
                    .setParameter("level", FIRST_QUESTION_LEVEL)
                    .getSingleResult();
            Assertions.assertThat(data.content).isEqualTo(question.getContent());
            Assertions.assertThat(data.title).isEqualTo(question.getTitle());
            Assertions.assertThat(data.level).isEqualTo(FIRST_QUESTION_LEVEL);
            Assertions.assertThat(data.meAnswered).isFalse();
            Assertions.assertThat(data.partnerAnswered).isFalse();
            Assertions.assertThat(data.createdAt).isNotNull();
        }

        @Test
        @DisplayName("커플인 멤버 오늘의 질문 조회 성공 - 내 답변이 작성된 경우")
        void 커플_멤버_오늘의_질문_조회_성공_내_답변_작성() throws Exception {
            // given
            QuestionEntity question = em.createQuery("SELECT q FROM QuestionEntity q WHERE q.level = :level", QuestionEntity.class)
                    .setParameter("level", FIRST_QUESTION_LEVEL)
                    .getSingleResult();

            TempCoupleQuestionEntity tempQuestion = TempCoupleQuestionEntity.builder()
                    .question(question)
                    .memberId(MemberEntityId.of(member.getId()))
                    .answer("내 답변")
                    .coupleQuestionState(CoupleQuestionState.ALIVE)
                    .build();

            em.persist(tempQuestion);
            em.flush();

            // when
            MvcResult mvcResult = mockMvc.perform(get("/questions/today")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(new MediaType("application", "json", StandardCharsets.UTF_8)))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            ResponseDto<GetQuestionResponse> responseDto = objectMapper.readValue(
                    responseContent,
                    new TypeReference<>() {}
            );

            GetQuestionResponse data = responseDto.data;

            Assertions.assertThat(data.content).isEqualTo(question.getContent());
            Assertions.assertThat(data.title).isEqualTo(question.getTitle());
            Assertions.assertThat(data.level).isEqualTo(FIRST_QUESTION_LEVEL);
            Assertions.assertThat(data.meAnswered).isTrue();
            Assertions.assertThat(data.partnerAnswered).isFalse();
            Assertions.assertThat(data.createdAt).isNotNull();
        }

        @Test
        @DisplayName("커플인 멤버 오늘의 질문 조회 성공 - 상대방 답변이 작성된 경우")
        void 커플_멤버_오늘의_질문_조회_성공_상대방_답변_작성() throws Exception {
            // given
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

            CoupleMemberEntity partnerCoupleMember = em.createQuery("SELECT cm FROM CoupleMemberEntity cm WHERE cm.coupleEntityId.value = :coupleId AND cm.memberEntityId.value = :memberId", CoupleMemberEntity.class)
                    .setParameter("coupleId", coupleId)
                    .setParameter("memberId", partner.getId())
                    .getSingleResult();

            CoupleQuestionEntity coupleQuestion = em.createQuery("SELECT cq FROM CoupleQuestionEntity cq WHERE cq.coupleEntityId.value = :coupleId", CoupleQuestionEntity.class)
                    .setParameter("coupleId", coupleId)
                    .getSingleResult();

            MemberAnswerEntity answer = MemberAnswerEntity.builder()
                    .coupleQuestionEntityId(CoupleQuestionEntityId.of(coupleQuestion.getId()))
                    .coupleMemberEntityId(CoupleMemberEntityId.of(partnerCoupleMember.getId()))
                    .answer("상대방 답변")
                    .memberAnswerState(MemberAnswerState.ALIVE)
                    .build();

            em.persist(answer);
            em.flush();

            // when
            MvcResult mvcResult = mockMvc.perform(get("/questions/today")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(new MediaType("application", "json", StandardCharsets.UTF_8)))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            ResponseDto<GetQuestionResponse> responseDto = objectMapper.readValue(
                    responseContent,
                    new TypeReference<>() {}
            );

            GetQuestionResponse data = responseDto.data;
            QuestionEntity question = em.createQuery("SELECT q FROM QuestionEntity q WHERE q.level = :level", QuestionEntity.class)
                    .setParameter("level", FIRST_QUESTION_LEVEL)
                    .getSingleResult();
            Assertions.assertThat(data.content).isEqualTo(question.getContent());
            Assertions.assertThat(data.title).isEqualTo(question.getTitle());
            Assertions.assertThat(data.level).isEqualTo(FIRST_QUESTION_LEVEL);
            Assertions.assertThat(data.meAnswered).isFalse();
            Assertions.assertThat(data.partnerAnswered).isTrue();
            Assertions.assertThat(data.createdAt).isNotNull();
        }

        @Test
        @DisplayName("커플인 멤버 오늘의 질문 조회 성공 - 전날 완성된 오늘의 질문 조회")
        void 커플_멤버_오늘의_질문_조회_성공_전날_완성된_오늘의_질문() throws Exception {
            // given
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

            CoupleQuestionEntity coupleQuestion = em.createQuery("SELECT cq FROM CoupleQuestionEntity cq WHERE cq.coupleEntityId.value = :coupleId", CoupleQuestionEntity.class)
                    .setParameter("coupleId", coupleId)
                    .getSingleResult();

            em.createQuery("UPDATE CoupleQuestionEntity cq SET cq.coupleQuestionState = :state, cq.bothAnsweredAt = :bothAnsweredAt WHERE cq.id = :id")
                    .setParameter("state", CoupleQuestionState.COMPLETED)
                    .setParameter("bothAnsweredAt", LocalDateTime.now().minusDays(1))
                    .setParameter("id", coupleQuestion.getId())
                    .executeUpdate();

            // when
            MvcResult mvcResult = mockMvc.perform(get("/questions/today")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(new MediaType("application", "json", StandardCharsets.UTF_8)))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            ResponseDto<GetQuestionResponse> responseDto = objectMapper.readValue(
                    responseContent,
                    new TypeReference<>() {}
            );

            // 이전 질문이 완료된 상태이므로, 오늘의 질문은 다음 레벨의 질문이 조회되어야 함
            GetQuestionResponse data = responseDto.data;
            QuestionEntity question = em.createQuery("SELECT q FROM QuestionEntity q WHERE q.level = :level", QuestionEntity.class)
                    .setParameter("level", FIRST_QUESTION_LEVEL + 1)
                    .getSingleResult();
            Assertions.assertThat(data.content).isEqualTo(question.getContent());
            Assertions.assertThat(data.title).isEqualTo(question.getTitle());
            Assertions.assertThat(data.level).isEqualTo(FIRST_QUESTION_LEVEL + 1);
            Assertions.assertThat(data.meAnswered).isFalse();
            Assertions.assertThat(data.partnerAnswered).isFalse();
            Assertions.assertThat(data.createdAt).isNotNull();
        }
    }

    @Nested
    @DisplayName("과거 질문 조회 기능 검증")
    class CoupleBeforeQuestionFeature {

        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public static class GetQuestionResponse {
            Long coupleQuestionId;
            String title;
            String content;
            int level;
            boolean meAnswered;
            boolean partnerAnswered;
            LocalDateTime createdAt;
        }

        @Test
        @DisplayName("커플인 멤버가 과거 질문 조회 성공")
        void 커플_멤버_과거_질문_조회_성공() throws Exception {
            // given
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

            CoupleQuestionEntity coupleQuestion = em.createQuery("SELECT cq FROM CoupleQuestionEntity cq WHERE cq.coupleEntityId.value = :coupleId", CoupleQuestionEntity.class)
                    .setParameter("coupleId", coupleId)
                    .getSingleResult();
            em.createQuery("UPDATE CoupleQuestionEntity cq SET cq.coupleQuestionState = :state, cq.bothAnsweredAt = :bothAnsweredAt WHERE cq.id = :id")
                    .setParameter("state", CoupleQuestionState.OUTDATED)
                    .setParameter("bothAnsweredAt", LocalDateTime.now().minusDays(3))
                    .setParameter("id", coupleQuestion.getId())
                    .executeUpdate();

            QuestionEntity question1 = em.createQuery("SELECT q FROM QuestionEntity q WHERE q.level = :level", QuestionEntity.class)
                    .setParameter("level", FIRST_QUESTION_LEVEL + 1)
                    .getSingleResult();

            QuestionEntity question2 = em.createQuery("SELECT q FROM QuestionEntity q WHERE q.level = :level", QuestionEntity.class)
                    .setParameter("level", FIRST_QUESTION_LEVEL + 2)
                    .getSingleResult();

            CoupleQuestionEntity coupleQuestion1 = CoupleQuestionEntity.builder()
                    .coupleEntityId(coupleQuestion.getCoupleEntityId())
                    .question(question1)
                    .coupleQuestionState(CoupleQuestionState.OUTDATED)
                    .bothAnsweredAt(LocalDateTime.now().minusDays(2))
                    .build();

            CoupleQuestionEntity coupleQuestion2 = CoupleQuestionEntity.builder()
                    .coupleEntityId(coupleQuestion.getCoupleEntityId())
                    .question(question2)
                    .coupleQuestionState(CoupleQuestionState.OUTDATED)
                    .bothAnsweredAt(LocalDateTime.now().minusDays(2))
                    .build();

            CoupleMemberEntity partnerCoupleMember = em.createQuery("SELECT cm FROM CoupleMemberEntity cm WHERE cm.coupleEntityId.value = :coupleId AND cm.memberEntityId.value = :memberId", CoupleMemberEntity.class)
                    .setParameter("coupleId", coupleId)
                    .setParameter("memberId", partner.getId())
                    .getSingleResult();

            em.persist(coupleQuestion1);
            em.persist(coupleQuestion2);

            MemberAnswerEntity answer = MemberAnswerEntity.builder()
                    .coupleQuestionEntityId(CoupleQuestionEntityId.of(coupleQuestion1.getId()))
                    .coupleMemberEntityId(CoupleMemberEntityId.of(partnerCoupleMember.getId()))
                    .answer("상대방 답변")
                    .memberAnswerState(MemberAnswerState.ALIVE)
                    .build();

            em.persist(answer);
            em.flush();
            // when
            MvcResult mvcResult = mockMvc.perform(get("/questions/" + question1.getLevel())
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = mvcResult.getResponse().getContentAsString();
            ResponseDto<GetQuestionResponse> responseDto = objectMapper.readValue(
                    responseContent,
                    new TypeReference<>() {}
            );

            GetQuestionResponse data = responseDto.data;
            Assertions.assertThat(data.content).isEqualTo(question1.getContent());
            Assertions.assertThat(data.title).isEqualTo(question1.getTitle());
            Assertions.assertThat(data.level).isEqualTo(FIRST_QUESTION_LEVEL + 1);
            Assertions.assertThat(data.meAnswered).isFalse();
            Assertions.assertThat(data.partnerAnswered).isTrue();
            Assertions.assertThat(data.createdAt).isNotNull();
        }

        @Test
        @DisplayName("커플이 아닌 멤버 과거 질문 조회 실패")
        void 커플_x_멤버_과거_질문_조회_실패() throws Exception {
            // when & then
            mockMvc.perform(get("/questions/" + 3)
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("message").value(NOT_COUPLE_MEMBER.getMessage()))
                    .andExpect(jsonPath("code").value(NOT_COUPLE_MEMBER.getCode()));
        }

        @Test
        @DisplayName("커플인 멤버가 현재 단계 이후의 질문 조회 실패")
        void 커플_멤버_현재_단계_이후의_질문_조회_실패() throws Exception {
            // given
            mockMvc.perform(post("/couples")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    CoupleRequestDtoFactory.createCoupleLinkRequestDto(partner.getInviteCodeEntityValue().getValue())
                            )))
                    .andExpect(status().isOk());

            // when & then
            mockMvc.perform(get("/questions/" + 3)
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(NO_SUCH_COUPLE_QUESTION.getMessage()))
                    .andExpect(jsonPath("code").value(NO_SUCH_COUPLE_QUESTION.getCode()));
        }
    }

    @Nested
    @DisplayName("질문 답변 기능 검증")
    class CoupleAnswerQuestionFeature {
        // TODO : 커플이 아닌 멤버 오늘의 질문 답변 등록 성공
        // TODO : 커플인 멤버 오늘의 질문 답변 등록 성공
        // TODO : 커플이 아닌 멤버 이미 답변을 단 상태에서 답변 등록 실패
        // TODO : 커플인 멤버 이미 답변을 단 상태에서 답변 등록 실패
        // TODO : 커플인 멤버 답변을 모두 단 후 24시간 이후 새로운 질문 생성 전 답변 등록 실패
    }

    @Nested
    @DisplayName("질문 답변 조회 기능 검증")
    class GetCoupleAnswerQuestionFeature {
        // TODO : 커플이 아닌 멤버 답변이 없는 경우 답변 조회 성공
        // TODO : 커플이 아닌 멤버 오늘의 질문 답변 조회 성공
        // TODO : 커플인 멤버 답변이 없는 경우 답변 조회 성공
        // TODO : 커플인 멤버 자신만 답변을 단 경우 오늘의 질문 답변 조회 성공
        // TODO : 커플인 멤버 상대방만 답변을 단 경우 오늘의 질문 답변 조회 성공
        // TODO : 커플인 멤버 답변을 모두 단 경우 오늘의 질문 답변 조회 성공
        // TODO : 커플인 멤버 과거 질문 답변 조회 성공
        // TODO : 커플인 멤버 다른 커플 소유의 질문 답변 조회 실패
    }

    @Nested
    @DisplayName("질문 답변 수정 기능 검증")
    class UpdateCoupleAnswerQuestionFeature {
        // TODO : 커플이 아닌 멤버 오늘의 질문 답변 수정 성공
        // TODO : 커플인 멤버 오늘의 질문 답변 수정 성공
        // TODO : 커플이 아닌 멤버 답변을 달지 않은 상태에서 답변 수정 실패
        // TODO : 커플인 멤버 답변을 달지 않은 상태에서 답변 수정 실패
        // TODO : 커플인 멤버 답변을 모두 단 후 24시간 이후 새로운 질문 생성 전 답변 수정 실패
    }


    @Nested
    @DisplayName("다양한 커플 연동 케이스 검증")
    class VariousCaseQuestionFeature {
        // TODO : 커플 연결이 끊어진 경우 오늘의 질문 조회 성공
        // TODO : 커플 연결이 끊어진 경우 오늘의 질문 답변 등록 성공
        // TODO : 커플 연결이 끊어진 경우 오늘의 질문 답변 수정 성공
        // TODO : 커플 연결이 끊어진 경우 과거 질문 조회 실패
        // TODO : 커플 연결이 끊어진 경우 과거 질문 답변 조회 실패

        // TODO : 커플 연결이 끊어진 이후 재결합한 경우 오늘의 질문 조회 성공
        // TODO : 커플 연결이 끊어진 이후 재결합한 경우 오늘의 질문 답변 등록 성공
        // TODO : 커플 연결이 끊어진 이후 재결합한 경우 오늘의 질문 답변 수정 성공
        // TODO : 커플 연결이 끊어진 이후 재결합한 경우 과거 질문 조회 성공

        // TODO : 커플 연결이 끊어진 이후 다른 사람과 연결한 경우 오늘의 질문 조회 성공
        // TODO : 커플 연결이 끊어진 이후 다른 사람과 연결한 경우 오늘의 질문 답변 등록 성공
        // TODO : 커플 연결이 끊어진 이후 다른 사람과 연결한 경우 오늘의 질문 답변 수정 성공
        // TODO : 커플 연결이 끊어진 이후 다른 사람과 연결한 경우 과거 질문 조회 성공
        // TODO : 커플 연결이 끊어진 이후 다른 사람과 연결한 경우 이전 커플 질문 조회 실패
        // TODO : 커플 연결이 끊어진 이후 다른 사람과 연결한 경우 이전 커플 질문 답변 조회 실패

        // TODO : 커플이 아닌 멤버가 오늘의 질문 답변 등록 후 커플 연동 시 답변 생성 확인
        // TODO : 커플이 아닌 멤버가 각자 오늘의 질문 답변 등록 후 커플 연동 시 답변 생성 및 완료 상태 확인
    }
}
