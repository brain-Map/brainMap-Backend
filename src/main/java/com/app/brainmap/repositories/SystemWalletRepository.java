package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.SystemWallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SystemWalletRepository extends JpaRepository<SystemWallet, UUID> {
    
    /**
     * Find wallet for a specific domain expert
     * Each domain expert has only ONE wallet
     */
    Optional<SystemWallet> findByBelongsToId(UUID domainExpertId);
    
    /**
     * Get all wallets with pagination
     */
    Page<SystemWallet> findAllByOrderByUpdatedAtDesc(Pageable pageable);
    
    /**
     * Get wallets by status
     */
    Page<SystemWallet> findByStatusOrderByUpdatedAtDesc(String status, Pageable pageable);
    
    /**
     * Increment wallet amount for a domain expert
     */
    @Modifying
    @Query("UPDATE SystemWallet sw SET sw.amount = sw.amount + :amountToAdd, sw.lastTransactionAt = :transactionTime, sw.updatedAt = :transactionTime WHERE sw.belongsTo.id = :domainExpertId")
    int incrementWalletAmount(@Param("domainExpertId") UUID domainExpertId, 
                              @Param("amountToAdd") Integer amountToAdd,
                              @Param("transactionTime") LocalDateTime transactionTime);
    
    /**
     * Check if wallet exists for domain expert
     */
    boolean existsByBelongsToId(UUID domainExpertId);
}
