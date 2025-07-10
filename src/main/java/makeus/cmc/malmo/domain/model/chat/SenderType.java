package makeus.cmc.malmo.domain.model.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SenderType {
    USER("user"), ASSISTANT("assistant");

    private final String apiName;

}