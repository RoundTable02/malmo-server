package makeus.cmc.malmo.domain.value.id;

import lombok.Value;

@Value
public class ChatRoomId {
    Long value;

    public static ChatRoomId of(Long value) {
        return new ChatRoomId(value);
    }
}
