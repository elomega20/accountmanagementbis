package groupeisi.com.accountManagement.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class SwaggerConf {
    @Bean
    public OpenAPI configOpenAPI() {
        return new OpenAPI().info(
            new Info()
                .title("REST API Documentation")
                .description("API Documentation ACCOUNT MANAGEMENT")
                .version("v1"));
    }
}
