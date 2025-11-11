package makeus.cmc.malmo.application.port.in.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SufficiencyCheckResult {
    private boolean completed;
    private String summary;
    private String advice;
}
