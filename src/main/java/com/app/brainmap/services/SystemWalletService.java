package com.app.brainmap.services;

import com.app.brainmap.domain.dto.wallet.SystemWalletResponse;
import com.app.brainmap.domain.dto.wallet.WalletBalanceResponse;
import com.app.brainmap.domain.dto.wallet.SystemWalletTotalsResponse;
import com.app.brainmap.domain.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SystemWalletService {
    
    SystemWalletResponse addToWallet(Transaction transaction);
    WalletBalanceResponse getWalletBalance(UUID domainExpertId);
    SystemWalletResponse getWallet(UUID domainExpertId);
    Page<SystemWalletResponse> getAllWallets(Pageable pageable);
    SystemWalletTotalsResponse getTotals();
}
