package makeus.cmc.malmo.domain.model.chat;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.member.Member;

@Getter
@SuperBuilder
@AllArgsConstructor
public class ChatRoom extends BaseTimeEntity {
    private Long id;
    private Member member;
    private ChatRoomState chatRoomState;
}
