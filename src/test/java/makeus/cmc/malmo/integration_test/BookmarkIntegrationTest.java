package makeus.cmc.malmo.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.BookmarkEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.ChatMessageEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.ChatRoomEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.application.port.out.member.GenerateTokenPort;
import makeus.cmc.malmo.domain.value.state.BookmarkState;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;
import makeus.cmc.malmo.domain.value.type.SenderType;
import makeus.cmc.malmo.integration_test.dto_factory.BookmarkRequestDtoFactory;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class BookmarkIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private GenerateTokenPort generateTokenPort;

    private String accessToken;
    private String otherAccessToken;

    private MemberEntity member;
    private MemberEntity otherMember;
    private ChatRoomEntity chatRoom;
    private ChatMessageEntity chatMessage;
    private ChatMessageEntity chatMessage2;

    @BeforeEach
    void setup() {
        member = createAndSaveMember("testUser", "test@email.com", "invite1");
        otherMember = createAndSaveMember("otherUser", "other@email.com", "invite2");

        chatRoom = ChatRoomEntity.builder()
                .memberEntityId(MemberEntityId.of(member.getId()))
                .chatRoomState(ChatRoomState.COMPLETED)
                .level(1)
                .detailedLevel(1)
                .build();
        em.persist(chatRoom);

        chatMessage = ChatMessageEntity.builder()
                .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                .content("테스트 메시지 1")
                .senderType(SenderType.USER)
                .level(1)
                .detailedLevel(1)
                .build();
        em.persist(chatMessage);

        chatMessage2 = ChatMessageEntity.builder()
                .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                .content("테스트 메시지 2")
                .senderType(SenderType.ASSISTANT)
                .level(1)
                .detailedLevel(1)
                .build();
        em.persist(chatMessage2);

        em.flush();

        accessToken = generateTokenPort.generateToken(member.getId(), member.getMemberRole()).getAccessToken();
        otherAccessToken = generateTokenPort.generateToken(otherMember.getId(), otherMember.getMemberRole()).getAccessToken();
    }

    @Nested
    @DisplayName("북마크 생성")
    class CreateBookmark {

        @Test
        @DisplayName("북마크 생성에 성공한다")
        void 북마크_생성_성공() throws Exception {
            BookmarkRequestDtoFactory.CreateBookmarkRequest request =
                    BookmarkRequestDtoFactory.createBookmarkRequest(chatMessage.getId());

            mockMvc.perform(post("/chatrooms/{chatRoomId}/bookmarks", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.bookmarkId").exists())
                    .andExpect(jsonPath("$.data.content").value("테스트 메시지 1"))
                    .andExpect(jsonPath("$.data.type").value("USER"));
        }

        @Test
        @DisplayName("이미 북마크된 메시지에 대해 북마크 생성에 실패한다")
        void 이미_북마크된_메시지_북마크_생성_실패() throws Exception {
            // Given: 이미 북마크 존재
            BookmarkEntity existingBookmark = BookmarkEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .chatMessageEntityId(ChatMessageEntityId.of(chatMessage.getId()))
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .bookmarkState(BookmarkState.ALIVE)
                    .build();
            em.persist(existingBookmark);
            em.flush();

            BookmarkRequestDtoFactory.CreateBookmarkRequest request =
                    BookmarkRequestDtoFactory.createBookmarkRequest(chatMessage.getId());

            mockMvc.perform(post("/chatrooms/{chatRoomId}/bookmarks", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(40015)); // BOOKMARK_ALREADY_EXISTS
        }

        @Test
        @DisplayName("다른 사용자의 채팅방에 대해 북마크 생성에 실패한다")
        void 다른_사용자_채팅방_북마크_생성_실패() throws Exception {
            BookmarkRequestDtoFactory.CreateBookmarkRequest request =
                    BookmarkRequestDtoFactory.createBookmarkRequest(chatMessage.getId());

            mockMvc.perform(post("/chatrooms/{chatRoomId}/bookmarks", chatRoom.getId())
                            .header("Authorization", "Bearer " + otherAccessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("존재하지 않는 메시지에 대해 북마크 생성에 실패한다")
        void 존재하지_않는_메시지_북마크_생성_실패() throws Exception {
            BookmarkRequestDtoFactory.CreateBookmarkRequest request =
                    BookmarkRequestDtoFactory.createBookmarkRequest(999999L);

            mockMvc.perform(post("/chatrooms/{chatRoomId}/bookmarks", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(40016)); // NO_SUCH_MESSAGE
        }
    }

    @Nested
    @DisplayName("북마크 삭제")
    class DeleteBookmarks {

        @Test
        @DisplayName("북마크 단건 삭제에 성공한다")
        void 북마크_단건_삭제_성공() throws Exception {
            // Given
            BookmarkEntity bookmark = BookmarkEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .chatMessageEntityId(ChatMessageEntityId.of(chatMessage.getId()))
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .bookmarkState(BookmarkState.ALIVE)
                    .build();
            em.persist(bookmark);
            em.flush();
            em.clear();

            BookmarkRequestDtoFactory.DeleteBookmarksRequest request =
                    BookmarkRequestDtoFactory.deleteBookmarksRequest(List.of(bookmark.getId()));

            mockMvc.perform(delete("/chatrooms/{chatRoomId}/bookmarks", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            BookmarkEntity deletedBookmark = em.find(BookmarkEntity.class, bookmark.getId());
            assertThat(deletedBookmark.getBookmarkState()).isEqualTo(BookmarkState.DELETED);
        }

        @Test
        @DisplayName("북마크 다건 삭제에 성공한다")
        void 북마크_다건_삭제_성공() throws Exception {
            // Given
            BookmarkEntity bookmark1 = BookmarkEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .chatMessageEntityId(ChatMessageEntityId.of(chatMessage.getId()))
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .bookmarkState(BookmarkState.ALIVE)
                    .build();
            BookmarkEntity bookmark2 = BookmarkEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .chatMessageEntityId(ChatMessageEntityId.of(chatMessage2.getId()))
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .bookmarkState(BookmarkState.ALIVE)
                    .build();
            em.persist(bookmark1);
            em.persist(bookmark2);
            em.flush();
            em.clear();

            BookmarkRequestDtoFactory.DeleteBookmarksRequest request =
                    BookmarkRequestDtoFactory.deleteBookmarksRequest(
                            List.of(bookmark1.getId(), bookmark2.getId()));

            mockMvc.perform(delete("/chatrooms/{chatRoomId}/bookmarks", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            assertThat(em.find(BookmarkEntity.class, bookmark1.getId()).getBookmarkState())
                    .isEqualTo(BookmarkState.DELETED);
            assertThat(em.find(BookmarkEntity.class, bookmark2.getId()).getBookmarkState())
                    .isEqualTo(BookmarkState.DELETED);
        }

        @Test
        @DisplayName("다른 사용자의 북마크 삭제에 실패한다")
        void 다른_사용자_북마크_삭제_실패() throws Exception {
            // Given: member's bookmark
            BookmarkEntity bookmark = BookmarkEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .chatMessageEntityId(ChatMessageEntityId.of(chatMessage.getId()))
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .bookmarkState(BookmarkState.ALIVE)
                    .build();
            em.persist(bookmark);
            em.flush();

            BookmarkRequestDtoFactory.DeleteBookmarksRequest request =
                    BookmarkRequestDtoFactory.deleteBookmarksRequest(List.of(bookmark.getId()));

            // otherMember tries to delete member's bookmark
            mockMvc.perform(delete("/chatrooms/{chatRoomId}/bookmarks", chatRoom.getId())
                            .header("Authorization", "Bearer " + otherAccessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("북마크 리스트 조회")
    class GetBookmarkList {

        @Test
        @DisplayName("북마크 리스트 조회에 성공한다")
        void 북마크_리스트_조회_성공() throws Exception {
            // Given
            em.persist(BookmarkEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .chatMessageEntityId(ChatMessageEntityId.of(chatMessage.getId()))
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .bookmarkState(BookmarkState.ALIVE)
                    .build());
            em.persist(BookmarkEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .chatMessageEntityId(ChatMessageEntityId.of(chatMessage2.getId()))
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .bookmarkState(BookmarkState.ALIVE)
                    .build());
            em.flush();

            mockMvc.perform(get("/chatrooms/{chatRoomId}/bookmarks", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.list").isArray())
                    .andExpect(jsonPath("$.data.list.length()").value(2));
        }

        @Test
        @DisplayName("삭제된 북마크는 조회되지 않는다")
        void 삭제된_북마크_미조회() throws Exception {
            // Given
            em.persist(BookmarkEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .chatMessageEntityId(ChatMessageEntityId.of(chatMessage.getId()))
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .bookmarkState(BookmarkState.DELETED) // 삭제된 상태
                    .build());
            em.flush();

            mockMvc.perform(get("/chatrooms/{chatRoomId}/bookmarks", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(0));
        }

        @Test
        @DisplayName("페이지네이션이 정상 동작한다")
        void 페이지네이션_동작() throws Exception {
            // Given: 15개의 북마크 생성
            for (int i = 0; i < 15; i++) {
                ChatMessageEntity msg = ChatMessageEntity.builder()
                        .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                        .content("메시지 " + i)
                        .senderType(SenderType.USER)
                        .level(1)
                        .detailedLevel(1)
                        .build();
                em.persist(msg);
                em.flush();

                em.persist(BookmarkEntity.builder()
                        .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                        .chatMessageEntityId(ChatMessageEntityId.of(msg.getId()))
                        .memberEntityId(MemberEntityId.of(member.getId()))
                        .bookmarkState(BookmarkState.ALIVE)
                        .build());
            }
            em.flush();

            mockMvc.perform(get("/chatrooms/{chatRoomId}/bookmarks", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(15))
                    .andExpect(jsonPath("$.data.list.length()").value(10));

            mockMvc.perform(get("/chatrooms/{chatRoomId}/bookmarks", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .param("page", "1")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.list.length()").value(5));
        }

        @Test
        @DisplayName("다른 사용자의 채팅방 북마크 조회에 실패한다")
        void 다른_사용자_채팅방_북마크_조회_실패() throws Exception {
            mockMvc.perform(get("/chatrooms/{chatRoomId}/bookmarks", chatRoom.getId())
                            .header("Authorization", "Bearer " + otherAccessToken))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("북마크 기반 메시지 조회")
    class GetMessagesByBookmark {

        @Test
        @DisplayName("북마크 기반 메시지 조회에 성공한다")
        void 북마크_기반_메시지_조회_성공() throws Exception {
            // Given
            BookmarkEntity bookmark = BookmarkEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .chatMessageEntityId(ChatMessageEntityId.of(chatMessage.getId()))
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .bookmarkState(BookmarkState.ALIVE)
                    .build();
            em.persist(bookmark);
            em.flush();

            mockMvc.perform(get("/chatrooms/{chatRoomId}/bookmarks/{bookmarkId}/messages",
                            chatRoom.getId(), bookmark.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .param("size", "10")
                            .param("sort", "ASC"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.targetMessageId").value(chatMessage.getId()))
                    .andExpect(jsonPath("$.data.messages").isArray());
        }

        @Test
        @DisplayName("다른 사용자의 북마크 기반 메시지 조회에 실패한다")
        void 다른_사용자_북마크_기반_메시지_조회_실패() throws Exception {
            // Given
            BookmarkEntity bookmark = BookmarkEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .chatMessageEntityId(ChatMessageEntityId.of(chatMessage.getId()))
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .bookmarkState(BookmarkState.ALIVE)
                    .build();
            em.persist(bookmark);
            em.flush();

            mockMvc.perform(get("/chatrooms/{chatRoomId}/bookmarks/{bookmarkId}/messages",
                            chatRoom.getId(), bookmark.getId())
                            .header("Authorization", "Bearer " + otherAccessToken)
                            .param("size", "10")
                            .param("sort", "ASC"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("존재하지 않는 북마크 기반 메시지 조회에 실패한다")
        void 존재하지_않는_북마크_메시지_조회_실패() throws Exception {
            mockMvc.perform(get("/chatrooms/{chatRoomId}/bookmarks/{bookmarkId}/messages",
                            chatRoom.getId(), 999999L)
                            .header("Authorization", "Bearer " + accessToken)
                            .param("size", "10")
                            .param("sort", "ASC"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(40014)); // NO_SUCH_BOOKMARK
        }
    }

    @Nested
    @DisplayName("채팅 메시지 리스트 조회 시 북마크 여부")
    class GetChatRoomMessagesWithBookmarkStatus {

        @Test
        @DisplayName("북마크된 메시지의 isSaved가 true로 반환된다")
        void 북마크된_메시지_isSaved_true() throws Exception {
            // Given
            em.persist(BookmarkEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .chatMessageEntityId(ChatMessageEntityId.of(chatMessage.getId()))
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .bookmarkState(BookmarkState.ALIVE)
                    .build());
            em.flush();

            mockMvc.perform(get("/chatrooms/{chatRoomId}/messages", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.list[?(@.messageId == " + chatMessage.getId() + ")].isSaved").value(true));
        }

        @Test
        @DisplayName("북마크되지 않은 메시지의 isSaved가 false로 반환된다")
        void 북마크되지_않은_메시지_isSaved_false() throws Exception {
            mockMvc.perform(get("/chatrooms/{chatRoomId}/messages", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.list[0].isSaved").value(false));
        }

        @Test
        @DisplayName("삭제된 북마크는 isSaved가 false로 반환된다")
        void 삭제된_북마크_isSaved_false() throws Exception {
            // Given: 삭제된 북마크
            em.persist(BookmarkEntity.builder()
                    .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                    .chatMessageEntityId(ChatMessageEntityId.of(chatMessage.getId()))
                    .memberEntityId(MemberEntityId.of(member.getId()))
                    .bookmarkState(BookmarkState.DELETED)
                    .build());
            em.flush();

            mockMvc.perform(get("/chatrooms/{chatRoomId}/messages", chatRoom.getId())
                            .header("Authorization", "Bearer " + accessToken)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.list[0].isSaved").value(false));
        }
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
}
