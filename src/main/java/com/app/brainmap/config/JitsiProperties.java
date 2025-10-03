package com.app.brainmap.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jitsi")
@Data
public class JitsiProperties {

    private String domain = "meet.jit.si";
    private String appId = "brainmap";
    private String appSecret = "your-app-secret-here";
    private Room room = new Room();

    @Data
    public static class Room {
        private String prefix = "brainmap";
    }
}
