package ferrefix.ms_ventas.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Configuración para implementar "Token Relay" en llamadas inter-servicios.
 * Este interceptor captura el token JWT de la petición original y lo propaga
 * en las peticiones salientes de Feign.
 */
@Configuration
public class FeignConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                // El token pasa de la llamada saliente hacia el Gateway/otro MS para que no suceda el error unauthorized
                template.header("Authorization", authorizationHeader);
            }
        }
    }
}
