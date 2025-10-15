package com.app.brainmap;

import com.app.brainmap.config.PayHereConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PayHereConfig.class)
public class BrainMapApplication {
    public static void main(String[] args) {
        SpringApplication.run(BrainMapApplication.class, args);
    }
}