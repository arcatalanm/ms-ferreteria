package ferrefix.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de rutas del Gateway de forma programática.
 * 
 * En Spring Cloud 2025.1.1 WebFlux, esta forma es más confiable
 * que usar YAML o properties para definir rutas.
 */
@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("ms-inventario", r -> r
                        .path("/api/inventario/**")
                        .uri("lb://ms-inventario"))
                .route("ms-usuarios", r -> r
                        .path("/api/usuarios/**")
                        .uri("lb://ms-usuarios"))
                .route("ms-ventas", r -> r
                        .path("/api/ventas/**")
                        .uri("lb://ms-ventas"))
                .route("ms-proveedores", r -> r
                        .path("/api/proveedores/**")
                        .uri("lb://ms-proveedores"))
                .route("ms-direcciones", r -> r
                        .path("/api/direcciones/**")
                        .uri("lb://ms-direcciones"))
                .build();
    }
}
