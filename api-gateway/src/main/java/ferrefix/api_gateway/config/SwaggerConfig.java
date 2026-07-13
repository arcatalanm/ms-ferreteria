package ferrefix.api_gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FerreFix — API Gateway")
                        .version("1.0")
                        .description("Endpoints del microservicio de Gateway de FerreFix"))
                .servers(List.of(new Server().url("/")));
    }
}
