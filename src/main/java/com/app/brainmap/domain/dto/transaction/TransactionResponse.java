package com.app.brainmap.domain.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    
    private UUID transactionId;
    private Integer amount;
    private UUID senderId;
    private String senderName;
    private UUID receiverId;
    private String receiverName;
    private String status;
    private LocalDateTime createdAt;
    private String paymentId; // Payment session ID if linked
}
