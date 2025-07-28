package makeus.cmc.malmo.integration_test.dto_factory;

import makeus.cmc.malmo.adaptor.in.web.controller.CoupleController;

public class CoupleRequestDtoFactory {

    public static CoupleController.CoupleLinkRequestDto createCoupleLinkRequestDto(String coupleCode) {
        CoupleController.CoupleLinkRequestDto dto = new CoupleController.CoupleLinkRequestDto();
        dto.setCoupleCode(coupleCode);
        return dto;
    }
}
