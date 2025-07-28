package makeus.cmc.malmo.integration_test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.question.CoupleQuestionEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.application.port.out.GenerateTokenPort;
import makeus.cmc.malmo.domain.value.state.CoupleQuestionState;
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

import static makeus.cmc.malmo.domain.service.CoupleQuestionDomainService.FIRST_QUESTION_LEVEL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class CoupleIntegrationTest {

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

        accessToken = tokenInfo.getAccessToken();
    }

    @Nested
    @DisplayName("회원가입 기능 검증")
    class CoupleLinkFeature {
        @Test
        @DisplayName("정상적인 요청의 경우 커플 연결이 성공한다.")
        void 커플_연결_성공() throws Exception {
            MvcResult mvcResult = mockMvc.perform(post("/couples")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    CoupleRequestDtoFactory.createCoupleLinkRequestDto("invite2")
                            )))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseContent = mvcResult.getResponse().getContentAsString();
            System.out.println("Response Content: " + responseContent);
            Integer coupleId = JsonPath.read(responseContent, "$.data.coupleId");

            // 커플 생성 여부 확인
            Assertions.assertThat(coupleId).isNotNull();
            CoupleEntity couple = em.createQuery("SELECT c FROM CoupleEntity c WHERE c.id = :coupleId", CoupleEntity.class)
                    .setParameter("coupleId", coupleId)
                    .getSingleResult();

            // 커플 멤버 생성 여부 확인
            Assertions.assertThat(couple.getCoupleMembers()).hasSize(2);
            Assertions.assertThat(couple.getCoupleState()).isEqualTo(CoupleState.ALIVE);

            // 연애 시작 날짜는 초대 코드 주인의 날짜를 따라가야 한다
            Assertions.assertThat(couple.getStartLoveDate()).isEqualTo(partner.getStartLoveDate());

            // 커플 멤버의 memberEntityId가 member, partner의 id에 속하는지 확인
            Assertions.assertThat(couple.getCoupleMembers().stream()
                    .anyMatch(cm -> cm.getMemberEntityId().getValue().equals(member.getId()))).isTrue();
            Assertions.assertThat(couple.getCoupleMembers().stream()
                    .anyMatch(cm -> cm.getMemberEntityId().getValue().equals(partner.getId()))).isTrue();

            // 커플 질문이 생성되었는지 확인
            CoupleQuestionEntity coupleQuestion = em.createQuery("SELECT cq FROM CoupleQuestionEntity cq WHERE cq.coupleEntityId.value = :coupleId", CoupleQuestionEntity.class)
                    .setParameter("coupleId", couple.getId())
                    .getSingleResult();
            Assertions.assertThat(coupleQuestion).isNotNull();
            Assertions.assertThat(coupleQuestion.getQuestion()).isNotNull();
            Assertions.assertThat(coupleQuestion.getQuestion().getLevel()).isEqualTo(FIRST_QUESTION_LEVEL);
            Assertions.assertThat(coupleQuestion.getCoupleQuestionState()).isEqualTo(CoupleQuestionState.ALIVE);
        }

        // TODO : 정지된 채팅방이 있는 경우 활성화
        // TODO : 재결합 커플인 경우 데이터 복구

        // TODO : 탈퇴한 사용자의 경우 연결 실패
        // TODO : 사용자가 이미 커플인 경우 연결 실패
        // TODO : 초대 코드가 이미 사용된 경우 연결 실패
        // TODO : 초대 코드가 없는 경우 연결 실패
        // TODO : 초대 코드가 잘못된 형식인 경우 연결 실패
        // TODO : 내 초대 코드로 커플 연결 시도 시 실패
    }


}
