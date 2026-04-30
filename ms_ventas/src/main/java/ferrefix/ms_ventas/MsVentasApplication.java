package ferrefix.ms_ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MsVentasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsVentasApplication.class, args);
	}

	// Bean para RestTemplate, que se usará para hacer llamadas a otros microservicios
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}	
}
