package makeus.cmc.malmo.application.port.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import makeus.cmc.malmo.domain.model.value.MemberId;

public interface ConnectSsePort {
    void connect(MemberId memberId);
}

