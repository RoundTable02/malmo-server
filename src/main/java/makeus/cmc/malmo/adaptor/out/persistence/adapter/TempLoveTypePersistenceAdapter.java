package makeus.cmc.malmo.adaptor.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.TempLoveTypeEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.TempLoveTypeMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.TempLoveTypeRepository;
import makeus.cmc.malmo.application.port.out.SaveTempLoveTypePort;
import makeus.cmc.malmo.domain.model.love_type.TempLoveType;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TempLoveTypePersistenceAdapter implements SaveTempLoveTypePort {

    private final TempLoveTypeMapper tempLoveTypeMapper;
    private final TempLoveTypeRepository tempLoveTypeRepository;

    @Override
    public TempLoveType saveTempLoveType(TempLoveType tempLoveType) {
        TempLoveTypeEntity entity = tempLoveTypeMapper.toEntity(tempLoveType);
        TempLoveTypeEntity saved = tempLoveTypeRepository.save(entity);
        return tempLoveTypeMapper.toDomain(saved);
    }
}
