package makeus.cmc.malmo.domain.model.value;

import lombok.Value;

@Value
public class ChatRoomId {
    Long value;

    public static ChatRoomId of(Long value) {
        return new ChatRoomId(value);
    }
}
