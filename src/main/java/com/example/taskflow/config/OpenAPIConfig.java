package com.example.taskflow.config;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI taskFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TaskFlow API")
                        .description(
                                "A complete task management REST API. " +
                                        "Manage your tasks, categories, and " +
                                        "track your productivity."
                        )
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Your Name")
                                .email("your@email.com"))
                        .license(new License()
                                .name("MIT License"))
                )
                // Register a JWT bearer scheme so Swagger UI shows an "Authorize" button.
                // NOT applied globally — only the protected controllers/methods declare
                // @SecurityRequirement("bearerAuth"), so public endpoints (login, register)
                // stay lock-free in the docs.
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste the token from /auth/login (no 'Bearer ' prefix)")));
    }
}
