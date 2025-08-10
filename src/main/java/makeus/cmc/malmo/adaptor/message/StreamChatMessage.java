package makeus.cmc.malmo.adaptor.message;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class StreamChatMessage implements StreamMessage {
    private Long memberId;
    private Long chatRoomId;
    private String nowMessage;
    private Integer promptLevel;
}
