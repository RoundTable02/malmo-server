package makeus.cmc.malmo.domain.model.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@NoArgsConstructor
@Entity
public class ChatMessage extends BaseTimeEntity {

    @Column(name = "chatMessageId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private boolean isImage;

    private String imageUrl;

    private String extractedText;

    @Enumerated(EnumType.STRING)
    private SenderType senderType;
}
