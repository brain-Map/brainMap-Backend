package com.app.brainmap.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {
    
    private String frontendUrl = "http://localhost:3000";
    private String backendUrl = "http://localhost:8082";
}
