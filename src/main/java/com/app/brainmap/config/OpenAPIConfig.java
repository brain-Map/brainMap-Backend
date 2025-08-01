package com.busmate.routeschedule.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Busmate Routes and Schedules API")
                        .version("1.0.0")
                        .description("API for managing routes, schedules, stops, permits, and related entities for NTC Planning Section. " +
                                "Use the Authorize button to enter a JWT token obtained from Supabase login."))
                // Add both localhost and EC2 servers for flexibility
                .addServersItem(new Server().url("http://47.128.250.151:8080").description("AWS EC2 Production"))
                .addServersItem(new Server().url("http://localhost:8080").description("Local Development"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token obtained from Supabase login")));
    }
}
