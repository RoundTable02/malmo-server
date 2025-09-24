package makeus.cmc.malmo.application.port.out.chat;

import makeus.cmc.malmo.adaptor.out.PendingMessageDto;

import java.time.Duration;
import java.util.List;

public interface LoadPendingMessagePort {
    List<PendingMessageDto> loadPendingMessages(Duration minIdleTime);
}
