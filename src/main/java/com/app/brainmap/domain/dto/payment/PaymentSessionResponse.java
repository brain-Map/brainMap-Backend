package com.app.brainmap.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSessionResponse {
    private String paymentId;
    private String redirectUrl;
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String message;
}
