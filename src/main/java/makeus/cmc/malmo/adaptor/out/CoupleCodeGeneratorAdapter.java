package makeus.cmc.malmo.adaptor.out;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.application.port.out.GenerateInviteCodePort;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CoupleCodeGeneratorAdapter implements GenerateInviteCodePort {

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 7;
    private final Random random = new SecureRandom();

    @Override
    public String generateInviteCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }
}
