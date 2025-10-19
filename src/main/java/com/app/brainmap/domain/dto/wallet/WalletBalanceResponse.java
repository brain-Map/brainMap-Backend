package com.app.brainmap.domain.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletBalanceResponse {
    
    private UUID domainExpertId;
    private String domainExpertName;
    private Integer totalBalance;
    private Integer pendingAmount;
    private Integer withdrawnAmount;
    private Long transactionCount;
}
