package ferrefix.ms_sugerencia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FerreFix — API Sugerencias")
                        .version("1.0")
                        .description("Endpoints del microservicio de sugerencias de FerreFix"));
    }
}
