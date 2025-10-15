package com.app.brainmap.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {
    
    private Frontend frontend = new Frontend();
    private Backend backend = new Backend();
    
    public String getFrontendUrl() {
        return frontend.getUrl();
    }
    
    public String getBackendUrl() {
        return backend.getUrl();
    }
    
    @Data
    public static class Frontend {
        private String url; // Will be populated from application-development.properties
    }
    
    @Data
    public static class Backend {
        private String url; // Will be populated from application-development.properties
    }
}