package makeus.cmc.malmo;

import jakarta.persistence.EntityManager;
import makeus.cmc.malmo.adaptor.out.persistence.entity.AESGCMConverter;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatMessageEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.ChatRoomEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.ChatRoomEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;
import makeus.cmc.malmo.domain.value.type.SenderType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Transactional
@SpringBootTest
public class AESGCMConverterTest {

    @Autowired
    private AESGCMConverter aesgcmConverter;

    private MemberEntity member;
    private ChatRoomEntity chatRoom;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setup() {
        member = createAndSaveMember("testUser", "test@email.com", "invite1");
        chatRoom = ChatRoomEntity.builder()
                .memberEntityId(MemberEntityId.of(member.getId()))
                .level(1)
                .chatRoomState(ChatRoomState.ALIVE)
                .build();
    }

    @Test
    void testEncryptionDecryption() {
        String original = "This is a test message.";
        String encrypted = aesgcmConverter.convertToDatabaseColumn(original);
        String decrypted = aesgcmConverter.convertToEntityAttribute(encrypted);
        Assertions.assertThat(decrypted).isEqualTo(original);
        Assertions.assertThat(encrypted).isNotEqualTo(original);

        System.out.println("Original: " + original);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + decrypted);
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

    @Test
    void testSaveChatMessageEntity() {
        // Given
        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .chatRoomEntityId(ChatRoomEntityId.of(chatRoom.getId()))
                .content("안녕하세요! AI 테스트 메시지입니다.")
                .level(1)
                .senderType(SenderType.USER)
                .build();

        em.persist(chatMessage);

        // When
        em.flush();
        em.clear();
        ChatMessageEntity retrieved = em.find(ChatMessageEntity.class, chatMessage.getId());

        // Then
        Assertions.assertThat(retrieved).isNotNull();
        Assertions.assertThat(retrieved.getContent()).isEqualTo(chatMessage.getContent());

        String encryptedContentFromDB = (String) em.createNativeQuery(
                        "SELECT content FROM chat_message_entity WHERE chat_message_id = ?")
                .setParameter(1, chatMessage.getId())
                .getSingleResult();

        Assertions.assertThat(encryptedContentFromDB).isNotEqualTo(chatMessage.getContent());
        System.out.println("Encrypted content in DB: " + encryptedContentFromDB);
    }

}
