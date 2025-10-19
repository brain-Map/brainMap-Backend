package com.app.brainmap.services;

import com.app.brainmap.domain.dto.wallet.SystemWalletResponse;
import com.app.brainmap.domain.dto.wallet.WalletBalanceResponse;
import com.app.brainmap.domain.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SystemWalletService {
    
    /**
     * Add amount to domain expert's wallet when a transaction is created
     * This method is called automatically when a transaction is inserted
     * If wallet doesn't exist, it creates one. If it exists, it adds to existing balance.
     * @param transaction The transaction that was created
     * @return Updated wallet response
     */
    SystemWalletResponse addToWallet(Transaction transaction);
    
    /**
     * Get wallet balance for a domain expert
     * @param domainExpertId Domain expert user ID
     * @return Balance information
     */
    WalletBalanceResponse getWalletBalance(UUID domainExpertId);
    
    /**
     * Get wallet for a domain expert
     * @param domainExpertId Domain expert user ID
     * @return Wallet details
     */
    SystemWalletResponse getWallet(UUID domainExpertId);
    
    /**
     * Get all wallets with pagination
     * @param pageable Pagination info
     * @return Page of wallets
     */
    Page<SystemWalletResponse> getAllWallets(Pageable pageable);
}
