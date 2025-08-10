package makeus.cmc.malmo.adaptor.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisEventListener implements MessageListener {
    private final RedisStreamConsumer consumer;

    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody());
        log.debug("PubSub received notification: {}", messageBody);
        consumer.onPubSubMessage(messageBody);
    }
}
