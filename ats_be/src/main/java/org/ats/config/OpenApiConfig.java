package org.ats.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
    return new OpenAPI()
            .info(new Info()
                    .title("Ats API Documentation")
                    .license(new License()
                            .name("ATS")
                            .url("https://github.com/ATS/Ats-API-Documentation")
                    )
                    .description("ATS API Documentation")
                    .contact(new Contact().email("ats.com.vn").name("ATS API Documentation").url("https://github.com/ATS/Ats-API-Documentation"))
            )
            ;
    }
}
