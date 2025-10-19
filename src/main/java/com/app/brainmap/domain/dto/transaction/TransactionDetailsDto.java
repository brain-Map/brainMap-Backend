package com.app.brainmap.domain.dto.transaction;

import com.app.brainmap.domain.PaymentStatus;
import com.app.brainmap.domain.PaymentType;
import com.app.brainmap.domain.UserRoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDetailsDto {
    private UUID transactionId;
    private String senderName;
    private String senderEmail;
    private UserRoleType senderRole;
    private String receiverName;
    private String receiverEmail;
    private UserRoleType receiverRole;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentType paymentType;
    private LocalDateTime createdAt;
    private String serviceListTitle;
}

