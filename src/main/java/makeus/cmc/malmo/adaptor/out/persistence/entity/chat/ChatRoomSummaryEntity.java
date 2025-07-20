package makeus.cmc.malmo.adaptor.out.persistence.entity.chat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntity;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoomSummaryEntity extends BaseTimeEntity {

    @Column(name = "chatRoomSummaryId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoomEntity chatRoom;

    private String content;
}
