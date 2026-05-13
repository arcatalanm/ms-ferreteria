package ferrefix.ms_proveedores;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// Anotacion para hacer llamados entre ms
@EnableFeignClients
@SpringBootApplication
public class MsProveedoresApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(MsProveedoresApplication.class, args);
	}

}
