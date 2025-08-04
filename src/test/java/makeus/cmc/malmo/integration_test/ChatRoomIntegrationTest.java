package makeus.cmc.malmo.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageSummaryEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.ChatRoomEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.application.port.out.GenerateTokenPort;
import makeus.cmc.malmo.application.service.chat.ChatProcessor;
import makeus.cmc.malmo.domain.model.chat.ChatRoom;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;
import makeus.cmc.malmo.domain.value.type.SenderType;
import makeus.cmc.malmo.integration_test.dto_factory.ChatRoomRequestDtoFactory;
import makeus.cmc.malmo.util.JosaUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static makeus.cmc.malmo.adaptor.in.exception.ErrorCode.*;
import static makeus.cmc.malmo.domain.model.chat.ChatRoomConstant.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class ChatRoomIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private GenerateTokenPort generateTokenPort;

    @MockBean
    private ChatProcessor chatProcessor;

    private String accessToken;
    private String otherAccessToken;

    private MemberEntity member;
    private MemberEntity otherMember;
    private MemberEntity deletedMember;

    @BeforeEach
    void setup() {
        member = createAndSaveMember("testUser", "test@email.com", "invite1");
        otherMember = createAndSaveMember("otherUser", "other@email.com", "invite2");
        deletedMember = createAndSaveDeletedMember("deletedUser", "deleted@email.com", "invite3");
        em.flush();

        accessToken = generateTokenPort.generateToken(member.getId(), member.getMemberRole()).getAccessToken();
        otherAccessToken = generateTokenPort.generateToken(otherMember.getId(), otherMember.getMemberRole()).getAccessToken();
    }

    private MemberEntity createAndSaveMember(String nickname, String email, String inviteCode) {
        MemberEntity memberEntity = MemberEntity.builder()
                .provider(Provider.KAKAO)
                .providerId(email) // providerId를 email로 사용
                .memberRole(MemberRole.MEMBER)
                .memberState(MemberState.ALIVE)
                .startLoveDate(LocalDate.of(2023, 1, 1)) // 임의의 연애 시작일
                .nickname(nickname)
                .email(email)
                .inviteCodeEntityValue(InviteCodeEntityValue.of(inviteCode))
                .build();
        em.persist(memberEntity);
        return memberEntity;
    }

    private MemberEntity createAndSaveDeletedMember(String nickname, String email, String inviteCode) {
        MemberEntity deletedMember = MemberEntity.builder()
                .provider(Provider.KAKAO)
                .providerId(email)
                .memberRole(MemberRole.MEMBER)
                .memberState(MemberState.DELETED)
                .nickname(nickname)
                .startLoveDate(LocalDate.of(2023, 1, 1)) // 임의의 연애 시작일
                .email(email)
                .inviteCodeEntityValue(InviteCodeEntityValue.of(inviteCode))
                .build();
        em.persist(deletedMember);
        return deletedMember;
    }

    @Nested
    @DisplayName("현재 채팅방 상태 조회")
    class GetCurrentChatRoom {
        @Test
        @DisplayName("채팅방이 없는 경우 채팅방 상태 조회에 성공하며, 새로운 채팅방이 생성된다")
        void 채팅방_없는_경우_상태_조회_성공() throws Exception {
            // when & then
            mockMvc.perform(get("/chatrooms/current")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.chatRoomState").value(ChatRoomState.BEFORE_INIT.name()));

            ChatRoomEntity chatRoom = em.createQuery("SELECT c FROM ChatRoomEntity c WHERE c.memberEntityId.value = :memberId", ChatRoomEntity.class)
                    .setParameter("memberId", member.getId())
                    .getSingleResult();
            Assertions.assertThat(chatRoom).isNotNull();
            Assertions.assertThat(chatRoom.getChatRoomState()).isEqualTo(ChatRoomState.BEFORE_INIT);

            List<ChatMessageEntity> messages = em.createQuery("SELECT m FROM ChatMessageEntity m WHERE m.chatRoomEntityId.value = :chatRoomId", ChatMessageEntity.class)
                    .setParameter("chatRoomId", chatRoom.getId())
                    .getResultList();
            Assertions.assertThat(messages).hasSize(1);
            Assertions.assertThat(messages.get(0).getContent()).isEqualTo(member.getNickname() + "아" + INIT_CHAT_MESSAGE);
        }

        @Test
        @DisplayName("채팅방이 있는 경우 채팅방 상태 조회에 성공한다")
        void 채팅방_있는_경우_상태_조회_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .build();
            em.persist(chatRoom);
            em.flush();

            // when & then
            mockMvc.perform(get("/chatrooms/current")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.chatRoomState").value(ChatRoomState.ALIVE.name()));
        }

        @Test
        @DisplayName("마지막 채팅 시간으로부터 24시간이 지난 경우 채팅방 상태 조회에 성공하며, 새로운 채팅방이 생성된다")
        void 마지막_채팅_시간_24시간_지난_경우_상태_조회_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .lastMessageSentTime(LocalDateTime.now().minusDays(1).minusHours(1)) // 25시간 전
                    .build();
            em.persist(chatRoom);
            em.flush();

            ChatProcessor.CounselingSummary mockSummary = new ChatProcessor.CounselingSummary(
                    "만료된 채팅방 요약",
                    "상황 키워드",
                    "솔루션 키워드"
            );
            when(chatProcessor.requestTotalSummary(any(), any(), any())).thenReturn(mockSummary);

            // when & then
            mockMvc.perform(get("/chatrooms/current")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.chatRoomState").value(ChatRoomState.BEFORE_INIT.name()));

            Assertions.assertThat(chatRoom.getChatRoomState()).isEqualTo(ChatRoomState.COMPLETED);
            Assertions.assertThat(chatRoom.getTotalSummary()).isEqualTo(ChatRoom.CREATING_SUMMARY_LINE);

            ChatRoomEntity newChatRoom = em.createQuery("SELECT c FROM ChatRoomEntity c WHERE c.memberEntityId.value = :memberId " +
                            "AND c.chatRoomState = :chatRoomState", ChatRoomEntity.class)
                    .setParameter("memberId", member.getId())
                    .setParameter("chatRoomState", ChatRoomState.BEFORE_INIT)
                    .getSingleResult();
            Assertions.assertThat(newChatRoom).isNotNull();

            List<ChatMessageEntity> messages = em.createQuery("SELECT m FROM ChatMessageEntity m WHERE m.chatRoomEntityId.value = :chatRoomId", ChatMessageEntity.class)
                    .setParameter("chatRoomId", newChatRoom.getId())
                    .getResultList();
            Assertions.assertThat(messages).hasSize(1);
            Assertions.assertThat(messages.get(0).getContent()).isEqualTo(member.getNickname() + "아" + INIT_CHAT_MESSAGE);
        }

        @Test
        @DisplayName("탈퇴한 사용자의 경우 채팅방 상태 조회에 실패한다")
        void 탈퇴한_사용자_상태_조회_실패() throws Exception {
            // when & then
            mockMvc.perform(get("/chatrooms/current")
                            .header("Authorization", "Bearer " + generateTokenPort.generateToken(deletedMember.getId(), deletedMember.getMemberRole()).getAccessToken()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_MEMBER.getCode()));
        }
    }

    @Nested
    @DisplayName("채팅 메시지 전송")
    class SendChatMessage {

        @Test
        @DisplayName("채팅방이 있는 경우 채팅 전송에 성공한다")
        void 채팅방_있는_경우_채팅_전송_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .level(1)
                    .build();
            em.persist(chatRoom);
            em.flush();

            String message = "안녕하세요";

            // Mock GptService
            doAnswer(invocation -> {
                Consumer<String> onComplete = invocation.getArgument(4);
                onComplete.accept("AI 응답입니다.");
                return null;
            }).when(chatProcessor).streamChat(any(), any(), any(), any(), any(), any());

            // when & then
            mockMvc.perform(post("/chatrooms/current/send")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createSendChatMessageRequestDto(message))))
                    .andExpect(status().isOk());

            List<ChatMessageEntity> messages = em.createQuery("SELECT m FROM ChatMessageEntity m WHERE m.chatRoomEntityId.value = :chatRoomId ORDER BY m.createdAt ASC", ChatMessageEntity.class)
                    .setParameter("chatRoomId", chatRoom.getId())
                    .getResultList();

            Assertions.assertThat(messages).hasSize(2);
            Assertions.assertThat(messages.get(0).getContent()).isEqualTo(message);
            Assertions.assertThat(messages.get(0).getSenderType()).isEqualTo(SenderType.USER);
            Assertions.assertThat(messages.get(1).getContent()).isEqualTo("AI 응답입니다.");
            Assertions.assertThat(messages.get(1).getSenderType()).isEqualTo(SenderType.ASSISTANT);
        }

        @Test
        @DisplayName("채팅방이 없는 경우 채팅 전송에 실패한다")
        void 채팅방_없는_경우_채팅_전송_실패() throws Exception {
            // when & then
            mockMvc.perform(post("/chatrooms/current/send")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createSendChatMessageRequestDto("hi"))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_CHAT_ROOM.getCode()));
        }

        @Test
        @DisplayName("탈퇴한 사용자의 경우 채팅 전송에 실패한다")
        void 탈퇴한_사용자_채팅_전송_실패() throws Exception {
            // when & then
            mockMvc.perform(post("/chatrooms/current/send")
                            .header("Authorization", "Bearer " + generateTokenPort.generateToken(deletedMember.getId(), deletedMember.getMemberRole()).getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createSendChatMessageRequestDto("hi"))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_MEMBER.getCode()));
        }

        @Test
        @DisplayName("채팅방이 시작 단계인 경우 채팅 전송에 성공한다")
        void 채팅방_시작_단계_채팅_전송_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.BEFORE_INIT)
                    .level(INIT_CHATROOM_LEVEL)
                    .build();
            em.persist(chatRoom);
            em.flush();

            String message = "시작 메시지";

            doAnswer(invocation -> {
                Consumer<String> onComplete = invocation.getArgument(4);
                onComplete.accept("AI 응답입니다.");
                return null;
            }).when(chatProcessor).streamChat(any(), any(), any(), any(), any(), any());

            // when & then
            mockMvc.perform(post("/chatrooms/current/send")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createSendChatMessageRequestDto(message))))
                    .andExpect(status().isOk());

            ChatRoomEntity updatedChatRoom = em.find(ChatRoomEntity.class, chatRoom.getId());
            Assertions.assertThat(updatedChatRoom.getChatRoomState()).isEqualTo(ChatRoomState.ALIVE);
        }

        @Test
        @DisplayName("채팅방이 마지막 단계인 경우 채팅 전송에 성공한다")
        void 채팅방_마지막_단계_채팅_전송_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .level(LAST_PROMPT_LEVEL)
                    .build();
            em.persist(chatRoom);
            em.flush();

            String message = "마지막 메시지";

            // when & then
            mockMvc.perform(post("/chatrooms/current/send")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createSendChatMessageRequestDto(message))))
                    .andExpect(status().isOk());

            List<ChatMessageEntity> messages = em.createQuery("SELECT m FROM ChatMessageEntity m WHERE m.chatRoomEntityId.value = :chatRoomId ORDER BY m.createdAt ASC", ChatMessageEntity.class)
                    .setParameter("chatRoomId", chatRoom.getId())
                    .getResultList();

            Assertions.assertThat(messages).hasSize(2);
            Assertions.assertThat(messages.get(0).getContent()).isEqualTo(message);
            Assertions.assertThat(messages.get(0).getSenderType()).isEqualTo(SenderType.USER);
            Assertions.assertThat(messages.get(1).getContent()).isEqualTo(FINAL_MESSAGE);
            Assertions.assertThat(messages.get(1).getSenderType()).isEqualTo(SenderType.ASSISTANT);
        }
    }

    @Nested
    @DisplayName("채팅방 업그레이드")
    class UpgradeChatRoom {
        @Test
        @DisplayName("채팅방 업그레이드에 성공한다")
        void 채팅방_업그레이드_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .level(1)
                    .build();
            em.persist(chatRoom);
            em.flush();

            // Mock GptService
            ArgumentCaptor<List<Map<String, String>>> summaryCaptor = ArgumentCaptor.forClass(List.class);
            doAnswer(invocation -> {
                Consumer<String> onSummary = invocation.getArgument(4);
                onSummary.accept("1단계 요약입니다.");
                return null;
            }).when(chatProcessor).requestSummaryAsync(summaryCaptor.capture(), any(), any(), any(), any());

            ArgumentCaptor<List<Map<String, String>>> streamCaptor = ArgumentCaptor.forClass(List.class);
            doAnswer(invocation -> {
                Consumer<String> onComplete = invocation.getArgument(4);
                onComplete.accept("다음 단계 오프닝 메시지입니다.");
                return null;
            }).when(chatProcessor).streamChat(streamCaptor.capture(), any(), any(), any(), any(), any());

            // when & then
            mockMvc.perform(post("/chatrooms/current/upgrade")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());

            ChatRoomEntity updatedChatRoom = em.find(ChatRoomEntity.class, chatRoom.getId());
            Assertions.assertThat(updatedChatRoom.getLevel()).isEqualTo(2);
            Assertions.assertThat(updatedChatRoom.getChatRoomState()).isEqualTo(ChatRoomState.ALIVE);

            ChatMessageSummaryEntity summary = em.createQuery("SELECT s FROM ChatMessageSummaryEntity s WHERE s.chatRoomEntityId.value = :chatRoomId", ChatMessageSummaryEntity.class)
                    .setParameter("chatRoomId", chatRoom.getId())
                    .getSingleResult();
            Assertions.assertThat(summary.getContent()).isEqualTo("1단계 요약입니다.");
            Assertions.assertThat(summary.getLevel()).isEqualTo(1);

            List<ChatMessageEntity> messages = em.createQuery("SELECT m FROM ChatMessageEntity m WHERE m.chatRoomEntityId.value = :chatRoomId AND m.level = 2", ChatMessageEntity.class)
                    .setParameter("chatRoomId", chatRoom.getId())
                    .getResultList();
            Assertions.assertThat(messages).hasSize(1);
            Assertions.assertThat(messages.get(0).getContent()).isEqualTo("다음 단계 오프닝 메시지입니다.");
        }

        @Test
        @DisplayName("탈퇴한 사용자의 경우 채팅방 업그레이드에 실패한다")
        void 탈퇴한_사용자_업그레이드_실패() throws Exception {
            // when & then
            mockMvc.perform(post("/chatrooms/current/upgrade")
                            .header("Authorization", "Bearer " + generateTokenPort.generateToken(deletedMember.getId(), deletedMember.getMemberRole()).getAccessToken()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_MEMBER.getCode()));
        }

        @Test
        @DisplayName("채팅방이 없는 경우 채팅방 업그레이드에 실패한다")
        void 채팅방_없는_경우_업그레이드_실패() throws Exception {
            // when & then
            mockMvc.perform(post("/chatrooms/current/upgrade")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_CHAT_ROOM.getCode()));
        }
    }

    @Nested
    @DisplayName("현재 채팅방 메시지 조회")
    class GetCurrentChatRoomMessages {
        @Test
        @DisplayName("현재 채팅방 메시지 조회에 성공한다")
        void 현재_채팅방_메시지_조회_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom = ChatRoomEntity.builder().memberEntityId(MemberEntityId.of(member.getId())).chatRoomState(ChatRoomState.ALIVE).build();
            em.persist(chatRoom);
            em.persist(ChatMessageEntity.builder().chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId())).level(1).senderType(SenderType.USER).content("메시지1").createdAt(LocalDateTime.now().minusMinutes(2)).build());
            em.persist(ChatMessageEntity.builder().chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId())).level(1).senderType(SenderType.ASSISTANT).content("메시지2").createdAt(LocalDateTime.now().minusMinutes(1)).build());
            em.flush();

            // when & then
            mockMvc.perform(get("/chatrooms/current/messages")
                            .header("Authorization", "Bearer " + accessToken)
                            .param("page", "0").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.list[0].content").value("메시지2")) // 최신순
                    .andExpect(jsonPath("$.data.list[1].content").value("메시지1"));
        }

        @Test
        @DisplayName("탈퇴한 사용자의 경우 현재 채팅방 메시지 조회에 실패한다")
        void 탈퇴한_사용자_메시지_조회_실패() throws Exception {
            // when & then
            mockMvc.perform(get("/chatrooms/current/messages")
                            .header("Authorization", "Bearer " + generateTokenPort.generateToken(deletedMember.getId(), deletedMember.getMemberRole()).getAccessToken()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_MEMBER.getCode()));
        }

        @Test
        @DisplayName("채팅방이 없는 경우 현재 채팅방 메시지 조회에 실패한다")
        void 채팅방_없는_경우_메시지_조회_실패() throws Exception {
            // when & then
            mockMvc.perform(get("/chatrooms/current/messages")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_CHAT_ROOM.getCode()));
        }
    }

    @Nested
    @DisplayName("채팅방 종료")
    class CompleteChatRoom {
        @Test
        @DisplayName("채팅방 종료에 성공한다")
        void 채팅방_종료_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom = ChatRoomEntity.builder().memberEntityId(MemberEntityId.of(member.getId())).chatRoomState(ChatRoomState.ALIVE).level(5).build();
            em.persist(chatRoom);
            em.persist(ChatMessageSummaryEntity.builder().chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId())).content("요약1").level(1).build());
            em.persist(ChatMessageSummaryEntity.builder().chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId())).content("요약2").level(2).build());
            em.flush();

            ChatProcessor.CounselingSummary summary = new ChatProcessor.CounselingSummary("최종 요약", "상황 키워드", "솔루션 키워드");
            when(chatProcessor.requestTotalSummary(any(), any(), any())).thenReturn(summary);

            // when & then
            mockMvc.perform(post("/chatrooms/current/complete")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());

            ChatRoomEntity completedChatRoom = em.find(ChatRoomEntity.class, chatRoom.getId());
            Assertions.assertThat(completedChatRoom.getChatRoomState()).isEqualTo(ChatRoomState.COMPLETED);
            Assertions.assertThat(completedChatRoom.getTotalSummary()).isEqualTo("최종 요약");
            Assertions.assertThat(completedChatRoom.getSituationKeyword()).isEqualTo("상황 키워드");
            Assertions.assertThat(completedChatRoom.getSolutionKeyword()).isEqualTo("솔루션 키워드");
        }

        @Test
        @DisplayName("채팅방이 없는 경우 채팅방 종료에 실패한다")
        void 채팅방_없는_경우_종료_실패() throws Exception {
            // when & then
            mockMvc.perform(post("/chatrooms/current/complete")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_CHAT_ROOM.getCode()));
        }

        @Test
        @DisplayName("탈퇴한 사용자의 경우 채팅방 종료에 실패한다")
        void 탈퇴한_사용자_종료_실패() throws Exception {
            // when & then
            mockMvc.perform(post("/chatrooms/current/complete")
                            .header("Authorization", "Bearer " + generateTokenPort.generateToken(deletedMember.getId(), deletedMember.getMemberRole()).getAccessToken()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_MEMBER.getCode()));
        }
    }

    @Nested
    @DisplayName("채팅방 리스트 조회")
    class GetChatRoomList {
        @Test
        @DisplayName("채팅방 리스트 조회에 성공한다")
        void 채팅방_리스트_조회_성공() throws Exception {
            // given
            em.persist(ChatRoomEntity.builder().memberEntityId(MemberEntityId.of(member.getId())).chatRoomState(ChatRoomState.COMPLETED).totalSummary("요약1").build());
            em.persist(ChatRoomEntity.builder().memberEntityId(MemberEntityId.of(member.getId())).chatRoomState(ChatRoomState.COMPLETED).totalSummary("요약2").build());
            em.flush();

            // when & then
            mockMvc.perform(get("/chatrooms")
                            .header("Authorization", "Bearer " + accessToken)
                            .param("page", "0").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.list[0].totalSummary").value("요약2")) // 최신순
                    .andExpect(jsonPath("$.data.list[1].totalSummary").value("요약1"));
        }

        @Test
        @DisplayName("탈퇴한 사용자의 경우 채팅방 리스트 조회에 실패한다")
        void 탈퇴한_사용자_리스트_조회_실패() throws Exception {
            // when & then
            mockMvc.perform(get("/chatrooms")
                            .header("Authorization", "Bearer " + generateTokenPort.generateToken(deletedMember.getId(), deletedMember.getMemberRole()).getAccessToken()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_MEMBER.getCode()));
        }

        @Test
        @DisplayName("채팅방이 없는 경우 채팅방 리스트 조회에 성공한다")
        void 채팅방_없는_경우_리스트_조회_성공() throws Exception {
            // when & then
            mockMvc.perform(get("/chatrooms")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(0))
                    .andExpect(jsonPath("$.data.list").isEmpty());
        }

        @Test
        @DisplayName("삭제한 채팅방이 있는 경우 채팅방 리스트 조회에 성공한다")
        void 삭제한_채팅방_있는_경우_리스트_조회_성공() throws Exception {
            // given
            em.persist(ChatRoomEntity.builder().memberEntityId(MemberEntityId.of(member.getId())).chatRoomState(ChatRoomState.COMPLETED).build());
            ChatRoomEntity deletedChatRoom = ChatRoomEntity.builder().memberEntityId(MemberEntityId.of(member.getId())).chatRoomState(ChatRoomState.DELETED).build();
            em.persist(deletedChatRoom);
            em.flush();

            // when & then
            mockMvc.perform(get("/chatrooms")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1)); // DELETED는 조회되지 않음
        }
    }

    @Nested
    @DisplayName("채팅방 삭제")
    class DeleteChatRoom {
        @Test
        @DisplayName("채팅방 한 건 삭제에 성공한다")
        void 채팅방_한건_삭제_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom = ChatRoomEntity.builder().memberEntityId(MemberEntityId.of(member.getId())).chatRoomState(ChatRoomState.COMPLETED).build();
            em.persist(chatRoom);
            em.flush();
            em.clear();

            // when & then
            mockMvc.perform(delete("/chatrooms")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createDeleteChatRoomsRequestDto(List.of(chatRoom.getId())))))
                    .andExpect(status().isOk());

            ChatRoomEntity deletedChatRoom = em.find(ChatRoomEntity.class, chatRoom.getId());
            Assertions.assertThat(deletedChatRoom.getChatRoomState()).isEqualTo(ChatRoomState.DELETED);
        }

        @Test
        @DisplayName("채팅방 여러 건 삭제에 성공한다")
        void 채팅방_여러건_삭제_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom1 = ChatRoomEntity.builder().memberEntityId(MemberEntityId.of(member.getId())).chatRoomState(ChatRoomState.COMPLETED).build();
            ChatRoomEntity chatRoom2 = ChatRoomEntity.builder().memberEntityId(MemberEntityId.of(member.getId())).chatRoomState(ChatRoomState.COMPLETED).build();
            em.persist(chatRoom1);
            em.persist(chatRoom2);
            em.flush();
            em.clear();

            // when & then
            mockMvc.perform(delete("/chatrooms")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createDeleteChatRoomsRequestDto(List.of(chatRoom1.getId(), chatRoom2.getId())))))
                    .andExpect(status().isOk());

            Assertions.assertThat(em.find(ChatRoomEntity.class, chatRoom1.getId()).getChatRoomState()).isEqualTo(ChatRoomState.DELETED);
            Assertions.assertThat(em.find(ChatRoomEntity.class, chatRoom2.getId()).getChatRoomState()).isEqualTo(ChatRoomState.DELETED);
        }

        @Test
        @DisplayName("접근 권한이 없으면 채팅방 삭제에 실패한다")
        void 접근_권한_없으면_삭제_실패() throws Exception {
            // given
            ChatRoomEntity otherChatRoom = ChatRoomEntity.builder().memberEntityId(MemberEntityId.of(otherMember.getId())).chatRoomState(ChatRoomState.COMPLETED).build();
            em.persist(otherChatRoom);
            em.flush();

            // when & then
            mockMvc.perform(delete("/chatrooms")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createDeleteChatRoomsRequestDto(List.of(otherChatRoom.getId())))))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(MEMBER_ACCESS_DENIED.getCode()));
        }

        @Test
        @DisplayName("탈퇴한 사용자의 경우 채팅방 삭제에 실패한다")
        void 탈퇴한_사용자_삭제_실패() throws Exception {
            // when & then
            mockMvc.perform(delete("/chatrooms")
                            .header("Authorization", "Bearer " + generateTokenPort.generateToken(deletedMember.getId(), deletedMember.getMemberRole()).getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createDeleteChatRoomsRequestDto(List.of(1L)))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_MEMBER.getCode()));
        }
    }

    @Nested
    @DisplayName("채팅방 메시지 리스트 조회")
    class GetChatRoomMessages {
        @Test
        @DisplayName("채팅방의 메시지 리스트 조회에 성공한다")
        void 메시지_리스트_조회_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom = ChatRoomEntity.builder().memberEntityId(MemberEntityId.of(member.getId())).chatRoomState(ChatRoomState.COMPLETED).build();
            em.persist(chatRoom);
            em.persist(ChatMessageEntity.builder().chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId())).level(1).senderType(SenderType.USER).content("메시지1").createdAt(LocalDateTime.now().minusMinutes(2)).build());
            em.persist(ChatMessageEntity.builder().chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId())).level(1).senderType(SenderType.ASSISTANT).content("메시지2").createdAt(LocalDateTime.now().minusMinutes(1)).build());
            em.flush();

            // when & then
            mockMvc.perform(get("/chatrooms/{chatRoomId}/messages", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .param("page", "0").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.list[0].content").value("메시지1")) // 오래된 순
                    .andExpect(jsonPath("$.data.list[1].content").value("메시지2"));
        }

        @Test
        @DisplayName("채팅방이 없는 경우 채팅방의 메시지 리스트 조회에 실패한다")
        void 채팅방_없는_경우_메시지_리스트_조회_실패() throws Exception {
            // when & then
            mockMvc.perform(get("/chatrooms/{chatRoomId}/messages", 999L)
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_CHAT_ROOM.getCode()));
        }

        @Test
        @DisplayName("채팅방 접근 권한이 없는 경우 채팅방의 메시지 리스트 조회에 실패한다")
        void 접근_권한_없는_경우_메시지_리스트_조회_실패() throws Exception {
            // given
            ChatRoomEntity otherChatRoom = ChatRoomEntity.builder().memberEntityId(MemberEntityId.of(otherMember.getId())).chatRoomState(ChatRoomState.COMPLETED).build();
            em.persist(otherChatRoom);
            em.flush();

            // when & then
            mockMvc.perform(get("/chatrooms/{chatRoomId}/messages", otherChatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(MEMBER_ACCESS_DENIED.getCode()));
        }

        @Test
        @DisplayName("탈퇴한 사용자의 경우 채팅방의 메시지 리스트 조회에 실패한다")
        void 탈퇴한_사용자_메시지_리스트_조회_실패() throws Exception {
            // when & then
            mockMvc.perform(get("/chatrooms/{chatRoomId}/messages", 1L)
                            .header("Authorization", "Bearer " + generateTokenPort.generateToken(deletedMember.getId(), deletedMember.getMemberRole()).getAccessToken()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_MEMBER.getCode()));
        }
    }

    @Nested
    @DisplayName("채팅방 요약 조회")
    class GetChatRoomSummary {
        @Test
        @DisplayName("채팅방 요약 조회에 성공한다")
        void 채팅방_요약_조회_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.COMPLETED)
                    .totalSummary("전체 요약")
                    .build();
            em.persist(chatRoom);
            em.persist(ChatMessageSummaryEntity.builder().chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId())).content("요약1").level(1).build());
            em.persist(ChatMessageSummaryEntity.builder().chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId())).content("요약2").level(2).build());
            em.persist(ChatMessageSummaryEntity.builder().chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId())).content("요약3").level(3).build());
            em.flush();

            // when & then
            mockMvc.perform(get("/chatrooms/{chatRoomId}/summary", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalSummary").value("전체 요약"))
                    .andExpect(jsonPath("$.data.firstSummary").value("요약1"))
                    .andExpect(jsonPath("$.data.secondSummary").value("요약2"))
                    .andExpect(jsonPath("$.data.thirdSummary").value("요약3"));
        }

        @Test
        @DisplayName("채팅방이 없는 경우 채팅방 요약 조회에 실패한다")
        void 채팅방_없는_경우_요약_조회_실패() throws Exception {
            // when & then
            mockMvc.perform(get("/chatrooms/{chatRoomId}/summary", 999L)
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_CHAT_ROOM.getCode()));
        }

        @Test
        @DisplayName("채팅방 접근 권한이 없는 경우 채팅방 요약 조회에 실패한다")
        void 접근_권한_없는_경우_요약_조회_실패() throws Exception {
            // given
            ChatRoomEntity otherChatRoom = ChatRoomEntity.builder().memberEntityId(MemberEntityId.of(otherMember.getId())).chatRoomState(ChatRoomState.COMPLETED).build();
            em.persist(otherChatRoom);
            em.flush();

            // when & then
            mockMvc.perform(get("/chatrooms/{chatRoomId}/summary", otherChatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(MEMBER_ACCESS_DENIED.getCode()));
        }

        @Test
        @DisplayName("탈퇴한 사용자의 경우 채팅방 요약 조회에 실패한다")
        void 탈퇴한_사용자_요약_조회_실패() throws Exception {
            // when & then
            mockMvc.perform(get("/chatrooms/{chatRoomId}/summary", 1L)
                            .header("Authorization", "Bearer " + generateTokenPort.generateToken(deletedMember.getId(), deletedMember.getMemberRole()).getAccessToken()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_MEMBER.getCode()));
        }
    }
}

