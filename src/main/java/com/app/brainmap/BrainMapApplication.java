package com.app.brainmap;

import com.app.brainmap.config.AppConfig;
import com.app.brainmap.config.PayHereConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({PayHereConfig.class, AppConfig.class})
public class BrainMapApplication {
    public static void main(String[] args) {
        SpringApplication.run(BrainMapApplication.class, args);
    }
}