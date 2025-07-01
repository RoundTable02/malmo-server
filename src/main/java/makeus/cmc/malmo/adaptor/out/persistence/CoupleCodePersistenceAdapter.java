package makeus.cmc.malmo.adaptor.out.persistence;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.couple.CoupleCodeEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.CoupleCodeMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.CoupleCodeRepository;
import makeus.cmc.malmo.application.port.out.LoadCoupleCodePort;
import makeus.cmc.malmo.application.port.out.SaveCoupleCodePort;
import makeus.cmc.malmo.domain.model.member.CoupleCode;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoupleCodePersistenceAdapter implements SaveCoupleCodePort, LoadCoupleCodePort {

    private final CoupleCodeMapper coupleCodeMapper;
    private final CoupleCodeRepository coupleCodeRepository;

    @Override
    public CoupleCode saveCoupleCode(CoupleCode coupleCode) {
        CoupleCodeEntity entity = coupleCodeMapper.toEntity(coupleCode);
        CoupleCodeEntity savedEntity = coupleCodeRepository.save(entity);
        return coupleCodeMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<CoupleCode> loadCoupleCodeByInviteCode(String inviteCode) {
        return coupleCodeRepository.findByInviteCode(inviteCode)
                .map(coupleCodeMapper::toDomain);
    }
}
