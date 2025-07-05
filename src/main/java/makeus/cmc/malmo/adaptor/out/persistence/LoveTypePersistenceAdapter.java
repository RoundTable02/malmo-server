package makeus.cmc.malmo.adaptor.out.persistence;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.LoveTypeMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.LoveTypeRepository;
import makeus.cmc.malmo.application.port.out.LoadLoveTypePort;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoveTypePersistenceAdapter implements LoadLoveTypePort {

    private final LoveTypeRepository loveTypeRepository;
    private final LoveTypeMapper loveTypeMapper;

    @Override
    public Optional<LoveType> findLoveTypeById(Long loveTypeId) {
        return loveTypeRepository.findById(loveTypeId)
            .map(loveTypeMapper::toDomain);
    }
}
