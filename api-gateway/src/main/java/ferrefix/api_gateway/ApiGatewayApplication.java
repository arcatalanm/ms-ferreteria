package ferrefix.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway para Ferrefix
 * 
 * Responsabilidades:
 * - Enrutar peticiones HTTP a microservicios basado en Path
 * - Resolver nombres de servicio vía Eureka (lb://ms-nombre)
 * - Aplicar filtros globales (logging, rate limiting, etc)
 * 
 * Stack: Spring Boot 4.0.6 + Spring Cloud 2025.1.1
 * Runtime: WebFlux (Netty), NO Tomcat
 * Puerto: 8000
 */
@SpringBootApplication
@EnableDiscoveryClient  // Explícito: registrarse en Eureka y descobrir otros servicios
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
