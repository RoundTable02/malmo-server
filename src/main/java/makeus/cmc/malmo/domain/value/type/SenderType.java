package makeus.cmc.malmo.domain.value.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SenderType {
    USER("user"), ASSISTANT("assistant"), SYSTEM("system");

    private final String apiName;

}