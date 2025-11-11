package makeus.cmc.malmo.adaptor.out;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class PendingMessageDto {
    private final String messageId;
    private final Long outboxId;
    private final Map<Object, Object> payload;
}
