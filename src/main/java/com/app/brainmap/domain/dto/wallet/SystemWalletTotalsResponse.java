package com.app.brainmap.domain.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemWalletTotalsResponse {
    private Long holdTotal;
    private Long releasedTotal;
    private Long systemChargedTotal;
    private Long withdrawnTotal;
}
