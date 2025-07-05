package makeus.cmc.malmo.application.port.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

public interface GetLoveTypeUseCase {

    GetLoveTypeResponseDto getLoveType(GetLoveTypeCommand command);

    @Data
    @Builder
    class GetLoveTypeCommand {
        private Long loveTypeId;
    }

    @Data
    @Builder
    class GetLoveTypeResponseDto {
        private Long loveTypeId;
        private String title;
        private String summary;
        private String content;
        private String imageUrl;
    }
}
