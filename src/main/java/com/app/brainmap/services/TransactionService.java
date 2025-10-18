package com.app.brainmap.services;

import com.app.brainmap.domain.dto.transaction.TransactionRequest;
import com.app.brainmap.domain.dto.transaction.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TransactionService {
    
    /**
     * Create a new transaction record
     * @param request Transaction details
     * @param authenticatedUserId The authenticated user making the request
     * @return Created transaction response
     */
    TransactionResponse createTransaction(TransactionRequest request, UUID authenticatedUserId);
    
    /**
     * Get transaction by ID
     * @param transactionId Transaction ID
     * @return Transaction response
     */
    TransactionResponse getTransactionById(UUID transactionId);
    
    /**
     * Get all transactions for a user (as sender or receiver)
     * @param userId User ID
     * @param pageable Pagination info
     * @return Page of transactions
     */
    Page<TransactionResponse> getUserTransactions(UUID userId, Pageable pageable);
    
    /**
     * Get transactions where user is sender
     * @param senderId Sender ID
     * @param pageable Pagination info
     * @return Page of transactions
     */
    Page<TransactionResponse> getSentTransactions(UUID senderId, Pageable pageable);
    
    /**
     * Get transactions where user is receiver
     * @param receiverId Receiver ID
     * @param pageable Pagination info
     * @return Page of transactions
     */
    Page<TransactionResponse> getReceivedTransactions(UUID receiverId, Pageable pageable);
}
