package ferrefix.ms_direcciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsDireccionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsDireccionesApplication.class, args);
	}

}
