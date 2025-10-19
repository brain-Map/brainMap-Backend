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
    private Integer holdAmount; // Amount on hold (within 14 days)
    private Integer releasedAmount; // Amount available for withdrawal (after 14 days)
    private Integer systemCharged; // Total system charges (5% of transactions)
    private Integer totalBalance; // holdAmount + releasedAmount
    private Integer totalReceived; // totalBalance + systemCharged
    private String status;
    private LocalDateTime lastTransactionAt;
    private LocalDateTime createdAt;
}
