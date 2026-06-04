package ferrefix.api_gateway.config;

/**
 * ⚠️ En Spring Cloud 2025.1.1, la configuración automática del LoadBalancer
 * con Eureka es suficiente. No es necesario bean explícito.
 * 
 * Lo que funciona:
 * 1. spring-cloud-starter-loadbalancer en pom.xml ✅
 * 2. spring.cloud.loadbalancer.eureka.enabled=true en properties ✅
 * 3. Rutas definidas programáticamente en GatewayRoutesConfig ✅
 */
public class GatewayLoadBalancerConfig {
    // Vacío - no se necesita configuración explícita
}

