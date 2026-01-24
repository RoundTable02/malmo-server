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
import makeus.cmc.malmo.application.helper.outbox.OutboxHelper;
import makeus.cmc.malmo.application.port.out.member.GenerateTokenPort;
import makeus.cmc.malmo.application.service.chat.ChatProcessor;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;
import makeus.cmc.malmo.domain.value.type.SenderType;
import makeus.cmc.malmo.integration_test.dto_factory.ChatRoomRequestDtoFactory;
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
import java.util.List;

import static makeus.cmc.malmo.adaptor.in.exception.ErrorCode.*;
import static makeus.cmc.malmo.util.GlobalConstants.*;
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

    @MockBean
    private OutboxHelper outboxHelper;

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
                .providerId(email)
                .memberRole(MemberRole.MEMBER)
                .memberState(MemberState.ALIVE)
                .startLoveDate(LocalDate.of(2023, 1, 1))
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
                .startLoveDate(LocalDate.of(2023, 1, 1))
                .email(email)
                .inviteCodeEntityValue(InviteCodeEntityValue.of(inviteCode))
                .build();
        em.persist(deletedMember);
        return deletedMember;
    }

    @Nested
    @DisplayName("채팅방 생성")
    class CreateChatRoom {
        @Test
        @DisplayName("채팅방 생성에 성공한다")
        void 채팅방_생성_성공() throws Exception {
            // when & then
            mockMvc.perform(post("/chatrooms")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.chatRoomId").exists())
                    .andExpect(jsonPath("$.data.chatRoomState").value(ChatRoomState.ALIVE.name()));

            List<ChatRoomEntity> chatRooms = em.createQuery("SELECT c FROM ChatRoomEntity c WHERE c.memberEntityId.value = :memberId", ChatRoomEntity.class)
                    .setParameter("memberId", member.getId())
                    .getResultList();
            Assertions.assertThat(chatRooms).hasSize(1);
            Assertions.assertThat(chatRooms.get(0).getChatRoomState()).isEqualTo(ChatRoomState.ALIVE);

            List<ChatMessageEntity> messages = em.createQuery("SELECT m FROM ChatMessageEntity m WHERE m.chatRoomEntityId.value = :chatRoomId", ChatMessageEntity.class)
                    .setParameter("chatRoomId", chatRooms.get(0).getId())
                    .getResultList();
            Assertions.assertThat(messages).hasSize(1);
            Assertions.assertThat(messages.get(0).getContent()).contains(INIT_CHAT_MESSAGE);
        }

        @Test
        @DisplayName("탈퇴한 사용자의 경우 채팅방 생성에 실패한다")
        void 탈퇴한_사용자_생성_실패() throws Exception {
            // when & then
            mockMvc.perform(post("/chatrooms")
                            .header("Authorization", "Bearer " + generateTokenPort.generateToken(deletedMember.getId(), deletedMember.getMemberRole()).getAccessToken()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_MEMBER.getCode()));
        }

        @Test
        @DisplayName("여러 개의 채팅방을 생성할 수 있다")
        void 다중_채팅방_생성_성공() throws Exception {
            // when - 첫 번째 채팅방 생성
            mockMvc.perform(post("/chatrooms")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());

            // when - 두 번째 채팅방 생성
            mockMvc.perform(post("/chatrooms")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());

            // then
            List<ChatRoomEntity> chatRooms = em.createQuery("SELECT c FROM ChatRoomEntity c WHERE c.memberEntityId.value = :memberId AND c.chatRoomState = :state", ChatRoomEntity.class)
                    .setParameter("memberId", member.getId())
                    .setParameter("state", ChatRoomState.ALIVE)
                    .getResultList();
            Assertions.assertThat(chatRooms).hasSize(2);
        }
    }

    @Nested
    @DisplayName("채팅 메시지 전송")
    class SendChatMessage {

        @Test
        @DisplayName("채팅방에 메시지 전송에 성공한다")
        void 메시지_전송_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .level(1)
                    .detailedLevel(1)
                    .build();
            em.persist(chatRoom);
            em.flush();

            String message = "안녕하세요";

            // when & then
            mockMvc.perform(post("/chatrooms/{chatRoomId}/messages", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createSendChatMessageRequestDto(message))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.messageId").exists());

            List<ChatMessageEntity> messages = em.createQuery("SELECT m FROM ChatMessageEntity m WHERE m.chatRoomEntityId.value = :chatRoomId ORDER BY m.createdAt ASC", ChatMessageEntity.class)
                    .setParameter("chatRoomId", chatRoom.getId())
                    .getResultList();

            Assertions.assertThat(messages).hasSize(1);
            Assertions.assertThat(messages.get(0).getContent()).isEqualTo(message);
            Assertions.assertThat(messages.get(0).getSenderType()).isEqualTo(SenderType.USER);
        }

        @Test
        @DisplayName("다른 사용자의 채팅방에 메시지 전송에 실패한다")
        void 권한_없는_채팅방_메시지_전송_실패() throws Exception {
            // given
            ChatRoomEntity otherChatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(otherMember.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .level(1)
                    .detailedLevel(1)
                    .build();
            em.persist(otherChatRoom);
            em.flush();

            // when & then
            mockMvc.perform(post("/chatrooms/{chatRoomId}/messages", otherChatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createSendChatMessageRequestDto("hi"))))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(MEMBER_ACCESS_DENIED.getCode()));
        }

        @Test
        @DisplayName("존재하지 않는 채팅방에 메시지 전송에 실패한다")
        void 존재하지_않는_채팅방_메시지_전송_실패() throws Exception {
            // when & then
            mockMvc.perform(post("/chatrooms/{chatRoomId}/messages", 999L)
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createSendChatMessageRequestDto("hi"))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_CHAT_ROOM.getCode()));
        }

        @Test
        @DisplayName("탈퇴한 사용자의 경우 메시지 전송에 실패한다")
        void 탈퇴한_사용자_메시지_전송_실패() throws Exception {
            // when & then
            mockMvc.perform(post("/chatrooms/{chatRoomId}/messages", 1L)
                            .header("Authorization", "Bearer " + generateTokenPort.generateToken(deletedMember.getId(), deletedMember.getMemberRole()).getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createSendChatMessageRequestDto("hi"))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NO_SUCH_MEMBER.getCode()));
        }

        @Test
        @DisplayName("DELETED 상태의 채팅방에 메시지 전송에 실패한다")
        void 삭제된_채팅방_메시지_전송_실패() throws Exception {
            // given
            ChatRoomEntity deletedChatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.DELETED)
                    .level(1)
                    .detailedLevel(1)
                    .build();
            em.persist(deletedChatRoom);
            em.flush();

            // when & then
            mockMvc.perform(post("/chatrooms/{chatRoomId}/messages", deletedChatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChatRoomRequestDtoFactory.createSendChatMessageRequestDto("hi"))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(NOT_VALID_CHAT_ROOM.getCode()));
        }
    }

    @Nested
    @DisplayName("채팅방 리스트 조회")
    class GetChatRoomList {
        @Test
        @DisplayName("채팅방 리스트 조회에 성공한다")
        void 채팅방_리스트_조회_성공() throws Exception {
            // given
            em.persist(ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .title("첫 번째 채팅방")
                    .lastMessageSentTime(LocalDateTime.now().minusHours(2))
                    .build());
            em.persist(ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .title("두 번째 채팅방")
                    .lastMessageSentTime(LocalDateTime.now().minusHours(1))
                    .build());
            em.flush();

            // when & then
            mockMvc.perform(get("/chatrooms")
                            .header("Authorization", "Bearer " + accessToken)
                            .param("page", "0").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.list[0].title").value("두 번째 채팅방"))
                    .andExpect(jsonPath("$.data.list[1].title").value("첫 번째 채팅방"));
        }

        @Test
        @DisplayName("ALIVE와 COMPLETED 상태 모두 조회된다")
        void ALIVE_COMPLETED_모두_조회() throws Exception {
            // given
            em.persist(ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .title("진행 중인 채팅방")
                    .lastMessageSentTime(LocalDateTime.now())
                    .build());
            em.persist(ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.COMPLETED)
                    .totalSummary("요약")
                    .lastMessageSentTime(LocalDateTime.now().minusHours(1))
                    .build());
            em.flush();

            // when & then
            mockMvc.perform(get("/chatrooms")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2));
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
        @DisplayName("삭제한 채팅방은 리스트에 조회되지 않는다")
        void 삭제한_채팅방_제외_리스트_조회() throws Exception {
            // given
            em.persist(ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .title("활성 채팅방")
                    .lastMessageSentTime(LocalDateTime.now())
                    .build());
            em.persist(ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.DELETED)
                    .title("삭제된 채팅방")
                    .lastMessageSentTime(LocalDateTime.now())
                    .build());
            em.flush();

            // when & then
            mockMvc.perform(get("/chatrooms")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1));
        }

        @Test
        @DisplayName("키워드로 채팅방을 검색할 수 있다")
        void 키워드_검색_성공() throws Exception {
            // given
            em.persist(ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .title("연애 고민")
                    .lastMessageSentTime(LocalDateTime.now())
                    .build());
            em.persist(ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .title("직장 고민")
                    .lastMessageSentTime(LocalDateTime.now())
                    .build());
            em.flush();

            // when & then
            mockMvc.perform(get("/chatrooms")
                            .header("Authorization", "Bearer " + accessToken)
                            .param("keyword", "연애"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andExpect(jsonPath("$.data.list[0].title").value("연애 고민"));
        }
    }

    @Nested
    @DisplayName("채팅방 삭제")
    class DeleteChatRoom {
        @Test
        @DisplayName("채팅방 한 건 삭제에 성공한다")
        void 채팅방_한건_삭제_성공() throws Exception {
            // given
            ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.COMPLETED)
                    .build();
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
            ChatRoomEntity chatRoom1 = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.COMPLETED)
                    .build();
            ChatRoomEntity chatRoom2 = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.COMPLETED)
                    .build();
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
            ChatRoomEntity otherChatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(otherMember.getId()))
                    .chatRoomState(ChatRoomState.COMPLETED)
                    .build();
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
            ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .build();
            em.persist(chatRoom);
            em.persist(ChatMessageEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .level(1)
                    .senderType(SenderType.USER)
                    .content("메시지1")
                    .createdAt(LocalDateTime.now().minusMinutes(2))
                    .build());
            em.persist(ChatMessageEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .level(1)
                    .senderType(SenderType.ASSISTANT)
                    .content("메시지2")
                    .createdAt(LocalDateTime.now().minusMinutes(1))
                    .build());
            em.flush();

            // when & then
            mockMvc.perform(get("/chatrooms/{chatRoomId}/messages", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .param("page", "0").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.list[0].content").value("메시지1"))
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
            ChatRoomEntity otherChatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(otherMember.getId()))
                    .chatRoomState(ChatRoomState.ALIVE)
                    .build();
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
            em.persist(ChatMessageSummaryEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .content("요약1")
                    .level(1)
                    .build());
            em.persist(ChatMessageSummaryEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .content("요약2")
                    .level(2)
                    .build());
            em.persist(ChatMessageSummaryEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .content("요약3")
                    .level(3)
                    .build());
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
            ChatRoomEntity otherChatRoom = ChatRoomEntity.builder()
                    .memberEntityId(MemberEntityId.of(otherMember.getId()))
                    .chatRoomState(ChatRoomState.COMPLETED)
                    .build();
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
