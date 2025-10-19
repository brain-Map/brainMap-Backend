package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    
    /**
     * Find all transactions where user is sender or receiver
     */
    @Query("SELECT t FROM Transaction t WHERE t.sender.id = :userId OR t.receiver.id = :userId ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    /**
     * Find all transactions where user is sender
     */
    Page<Transaction> findBySenderIdOrderByCreatedAtDesc(UUID senderId, Pageable pageable);
    
    /**
     * Find all transactions where user is receiver
     */
    Page<Transaction> findByReceiverIdOrderByCreatedAtDesc(UUID receiverId, Pageable pageable);
    
    /**
     * Find transactions by status
     */
    List<Transaction> findByStatus(String status);

    /**
     * Find all transaction details with eager loading of related entities
     * This query joins all necessary entities for TransactionDetailsDto
     */
    @Query("SELECT DISTINCT t FROM Transaction t " +
           "LEFT JOIN FETCH t.sender " +
           "LEFT JOIN FETCH t.receiver " +
           "LEFT JOIN FETCH t.paymentSession ps " +
           "LEFT JOIN FETCH ps.serviceBooking sb " +
           "LEFT JOIN FETCH sb.service " +
           "WHERE t.sender.id = :userId OR t.receiver.id = :userId " +
           "ORDER BY t.createdAt DESC")
    Page<Transaction> findTransactionDetailsForUser(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Find all transaction details (for admin or general listing)
     */
    @Query("SELECT DISTINCT t FROM Transaction t " +
           "LEFT JOIN FETCH t.sender " +
           "LEFT JOIN FETCH t.receiver " +
           "LEFT JOIN FETCH t.paymentSession ps " +
           "LEFT JOIN FETCH ps.serviceBooking sb " +
           "LEFT JOIN FETCH sb.service " +
           "ORDER BY t.createdAt DESC")
    Page<Transaction> findAllTransactionDetails(Pageable pageable);

    /**
     * Find transactions that are older than 14 days and haven't been released yet
     * Used by scheduled task to move amounts from hold to released
     * Accepts both 'SUCCESS' and 'COMPLETED' statuses
     */
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.amountReleased = false " +
           "AND t.createdAt <= :cutoffDate " +
           "AND (t.status = 'SUCCESS' OR t.status = 'COMPLETED')")
    List<Transaction> findUnreleasedTransactionsOlderThan(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
}
