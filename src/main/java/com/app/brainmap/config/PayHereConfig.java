package com.app.brainmap.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payhere")
@Data
public class PayHereConfig {
    
    private String mode = "sandbox";
    private String merchantId;
    private String merchantSecret;
    private String sandboxUrl = "https://sandbox.payhere.lk/pay/checkout";
    private String liveUrl = "https://www.payhere.lk/pay/checkout";
    
    public String getPayHereUrl() {
        return "sandbox".equals(mode) ? sandboxUrl : liveUrl;
    }
    
    public boolean isSandboxMode() {
        return "sandbox".equals(mode);
    }
    
    public boolean isLiveMode() {
        return "live".equals(mode);
    }
}
