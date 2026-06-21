package com.example.taskflow.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

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
                );
    }
}