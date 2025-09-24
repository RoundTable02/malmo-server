package makeus.cmc.malmo.application.port.out.chat;

import makeus.cmc.malmo.adaptor.message.StreamMessage;
import makeus.cmc.malmo.adaptor.message.StreamMessageType;
import makeus.cmc.malmo.domain.model.Outbox;

import java.util.List;

public interface PublishStreamMessagePort {
    String publish(StreamMessageType type, String payload, Long outboxId);
    List<String> publishBatch(List<Outbox> outboxList);

    void acknowledge(String messageId);
}
