package com.app.brainmap.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private UUID transactionId;
    private Integer amount;
    private UUID senderId;
    private UUID receiverId;
    private String status;
    private LocalDateTime createdAt;
    // Related payment session (optional)
    private UUID paymentSessionId;
}
