package com.example.baseoauth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;






@Configuration
public class OpenApiConfig {


    @Value("${spring.application.version}")
    private String APPLICATION_VERSION;


    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch("/**")
                .build();
    }


    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/**/AM*/**")
                .build();
    }


    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/**/AP*/**")
                .build();
    }


    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme apiKeySecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-API-Key");


        SecurityScheme bearerSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");


        SecurityRequirement securityRequirementForBearer = new SecurityRequirement().addList("bearerAuth");


        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("X-API-Key", apiKeySecurityScheme)
                        .addSecuritySchemes("bearerAuth", bearerSecurityScheme))
                .info(new Info()
                        .title("Tomo Server Application OAUTH")
                        .description("This is a Web OAUTH Description.")
                        .version(APPLICATION_VERSION))
                .addSecurityItem(securityRequirementForBearer);
    }
}

