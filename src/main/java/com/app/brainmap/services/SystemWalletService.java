package com.app.brainmap.services;

import com.app.brainmap.domain.dto.wallet.SystemWalletResponse;
import com.app.brainmap.domain.dto.wallet.WalletBalanceResponse;
import com.app.brainmap.domain.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SystemWalletService {
    
    /**
     * Create a wallet entry when a transaction is created
     * This method is called automatically when a transaction is inserted
     * @param transaction The transaction that was created
     * @return Created wallet entry response
     */
    SystemWalletResponse createWalletEntry(Transaction transaction);
    
    /**
     * Get wallet balance for a domain expert
     * @param domainExpertId Domain expert user ID
     * @return Balance information
     */
    WalletBalanceResponse getWalletBalance(UUID domainExpertId);
    
    /**
     * Get all wallet entries for a domain expert
     * @param domainExpertId Domain expert user ID
     * @param pageable Pagination info
     * @return Page of wallet entries
     */
    Page<SystemWalletResponse> getWalletEntries(UUID domainExpertId, Pageable pageable);
    
    /**
     * Get wallet entry by transaction ID
     * @param transactionId Transaction ID
     * @return Wallet entry
     */
    SystemWalletResponse getWalletEntryByTransaction(UUID transactionId);
}
