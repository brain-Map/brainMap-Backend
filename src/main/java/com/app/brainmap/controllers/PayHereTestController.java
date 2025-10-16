package com.app.brainmap.controllers;

import com.app.brainmap.config.PayHereConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class PayHereTestController {
    
    private final PayHereConfig payHereConfig;
    
    @GetMapping("/payhere-config")
    public Map<String, Object> getPayHereConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("mode", payHereConfig.getMode());
        config.put("merchantId", payHereConfig.getMerchantId());
        config.put("merchantSecretLength", payHereConfig.getMerchantSecret() != null ? payHereConfig.getMerchantSecret().length() : 0);
        config.put("sandboxUrl", payHereConfig.getSandboxUrl());
        config.put("liveUrl", payHereConfig.getLiveUrl());
        config.put("payHereUrl", payHereConfig.getPayHereUrl());
        config.put("isSandboxMode", payHereConfig.isSandboxMode());
        return config;
    }
}