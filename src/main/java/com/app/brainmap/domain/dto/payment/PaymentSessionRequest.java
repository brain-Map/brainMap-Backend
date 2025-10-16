package com.app.brainmap.domain.dto.payment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSessionRequest {
    
    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank
    @Builder.Default
    private String currency = "LKR";
    
    @NotBlank
    private String orderId;
    
    @NotBlank
    private String itemDescription;
    
    @NotBlank
    @Email
    private String customerEmail;
    
    @NotBlank
    private String customerName;
    
    private String customerPhone;
    private String customerAddress;
    
    @Builder.Default
    private String city = "Colombo";
    
    @Builder.Default
    private String country = "Sri Lanka";
    
    // PayHere configuration from frontend (optional, will use server defaults)
    private String payHereMode;
    private String payHereMerchantId;
    private String payHereApiUrl;
    private Boolean isSandbox;
}
