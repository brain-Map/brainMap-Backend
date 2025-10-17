package com.app.brainmap.domain.dto.payment;

import com.app.brainmap.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatusResponse {
    private String paymentId;
    private String orderId;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private String transactionId;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private String message;
    private String customerName;
    private String customerEmail;
}
