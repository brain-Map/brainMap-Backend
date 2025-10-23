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
public class SystemWalletResponse {
    
    private UUID walletId;
    private Integer holdAmount; // Amount on hold (within 14 days)
    private Integer releasedAmount; // Amount released (after 14 days, available for withdrawal)
    private Integer systemCharged; // Accumulated system charges (5% of transactions)
    private Integer withdrawnAmount; // Total amount withdrawn by the domain expert
    private UUID belongsTo; // Domain expert ID
    private String domainExpertName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastTransactionAt; // When last amount was added
}
