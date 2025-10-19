package com.app.brainmap.domain.dto.wallet;

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
public class WalletBalanceResponse {
    
    private UUID domainExpertId;
    private String domainExpertName;
    private Integer currentBalance; // Current accumulated balance (95% of transactions)
    private Integer systemCharged; // Total system charges (5% of transactions)
    private Integer totalReceived; // Total amount before system charge (currentBalance + systemCharged)
    private String status;
    private LocalDateTime lastTransactionAt;
    private LocalDateTime createdAt;
}
