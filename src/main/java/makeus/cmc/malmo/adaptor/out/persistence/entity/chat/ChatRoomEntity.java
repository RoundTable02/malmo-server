package makeus.cmc.malmo.adaptor.out.persistence.entity.chat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.value.state.ChatRoomCompletedReason;
import makeus.cmc.malmo.domain.value.state.ChatRoomState;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoomEntity extends BaseTimeEntity {

    @Column(name = "chatRoomId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private MemberEntityId memberEntityId;

    @Enumerated(EnumType.STRING)
    private ChatRoomState chatRoomState;

    private int level;

    private LocalDateTime lastMessageSentTime;

    @Column(columnDefinition = "TEXT")
    private String totalSummary;

    @Column(columnDefinition = "TEXT")
    private String situationKeyword;

    @Column(columnDefinition = "TEXT")
    private String solutionKeyword;

    @Enumerated(EnumType.STRING)
    private ChatRoomCompletedReason chatRoomCompletedReason;

    @Column(columnDefinition = "TEXT")
    private String counselingType;
}
