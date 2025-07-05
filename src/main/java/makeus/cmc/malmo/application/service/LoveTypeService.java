package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.LoveTypeNotFoundException;
import makeus.cmc.malmo.application.port.in.GetLoveTypeUseCase;
import makeus.cmc.malmo.application.port.out.LoadLoveTypePort;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoveTypeService implements GetLoveTypeUseCase {

    private final LoadLoveTypePort loadLoveTypePort;

    @Override
    public GetLoveTypeResponseDto getLoveType(GetLoveTypeCommand command) {
        LoveType loveType = loadLoveTypePort.findLoveTypeById(command.getLoveTypeId())
                .orElseThrow(LoveTypeNotFoundException::new);

        return GetLoveTypeResponseDto.builder()
                .loveTypeId(loveType.getId())
                .title(loveType.getTitle())
                .summary(loveType.getSummary())
                .content(loveType.getContent())
                .imageUrl(loveType.getImageUrl())
                .build();
    }
}
