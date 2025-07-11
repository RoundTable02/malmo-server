package makeus.cmc.malmo.adaptor.out.persistence.entity.chat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntityJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoomEntity extends BaseTimeEntityJpa {

    @Column(name = "chatRoomId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private MemberEntityId memberEntityId;

    @Enumerated(EnumType.STRING)
    private ChatRoomStateJpa chatRoomStateJpa;
}
