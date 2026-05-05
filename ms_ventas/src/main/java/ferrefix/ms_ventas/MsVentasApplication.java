package ferrefix.ms_ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD
import org.springframework.cloud.openfeign.EnableFeignClients;
=======
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
>>>>>>> branch_ms_gateway

@SpringBootApplication
@EnableFeignClients
public class MsVentasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsVentasApplication.class, args);
	}
<<<<<<< HEAD
=======

	// Bean para RestTemplate, que se usará para hacer llamadas a otros microservicios
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}	
>>>>>>> branch_ms_gateway
}
