package ferrefix.ms_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD
=======
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
>>>>>>> branch_ms_gateway

@SpringBootApplication
public class MsGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsGatewayApplication.class, args);
	}

<<<<<<< HEAD
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

=======
>>>>>>> branch_ms_gateway
}
