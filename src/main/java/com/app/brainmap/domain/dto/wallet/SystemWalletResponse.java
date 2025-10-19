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
    private Integer amount;
    private UUID belongsTo; // Domain expert ID
    private String domainExpertName;
    private UUID transactionId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime withdrawnAt;
}
