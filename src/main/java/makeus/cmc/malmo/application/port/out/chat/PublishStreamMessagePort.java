package makeus.cmc.malmo.application.port.out.chat;

import makeus.cmc.malmo.adaptor.message.StreamMessage;
import makeus.cmc.malmo.adaptor.message.StreamMessageType;

public interface PublishStreamMessagePort {
    void publish(StreamMessageType type, StreamMessage streamMessage);
}
