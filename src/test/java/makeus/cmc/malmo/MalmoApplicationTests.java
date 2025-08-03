package makeus.cmc.malmo;

import makeus.cmc.malmo.adaptor.out.jwt.TokenInfo;
import makeus.cmc.malmo.application.port.out.GenerateTokenPort;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MalmoApplicationTests {

	@Autowired
	private GenerateTokenPort generateTokenPort;

	@Test
	void contextLoads() {
		TokenInfo tokenInfo = generateTokenPort.generateToken(1L, MemberRole.MEMBER);
		System.out.println("Access Token: " + tokenInfo.getAccessToken());
	}

}
