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
    
    // Callback URLs (optional - can be overridden in properties)
    private String returnUrl;
    private String cancelUrl;
    private String notifyUrl;
    
    public String getPayHereUrl() {
        // For sandbox, use the exact sandbox checkout URL
        // For live, use the live checkout URL
        return "sandbox".equalsIgnoreCase(mode) ? sandboxUrl : liveUrl;
    }
    
    public boolean isSandboxMode() {
        return "sandbox".equals(mode);
    }
    
    public boolean isLiveMode() {
        return "live".equals(mode);
    }
}
