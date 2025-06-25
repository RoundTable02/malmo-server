package makeus.cmc.malmo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MalmoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalmoApplication.class, args);
	}

}
