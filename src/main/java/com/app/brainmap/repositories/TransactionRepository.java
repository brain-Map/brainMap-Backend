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
}
