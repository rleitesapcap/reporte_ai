package opus.social.app.reporteai.adapters.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Development"),
                new Server().url("http://localhost:9090").description("Local")
            ))
            .info(new Info()
                .title("Reporte AI API")
                .version("1.0.0")
                .description("API REST para a plataforma Reporte AI - Sistema inteligente de mapeamento de problemas urbanos e rurais")
                .contact(new Contact()
                    .name("Reporte AI Team")
                    .email("contact@reporteai.com")
                    .url("https://reporteai.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                .termsOfService("https://reporteai.com/terms"));
    }
}
