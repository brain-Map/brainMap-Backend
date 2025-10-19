package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.SystemWallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SystemWalletRepository extends JpaRepository<SystemWallet, UUID> {
    
    /**
     * Find all wallet entries for a specific domain expert
     */
    Page<SystemWallet> findByBelongsToIdOrderByCreatedAtDesc(UUID domainExpertId, Pageable pageable);
    
    /**
     * Find wallet entry by transaction ID
     */
    Optional<SystemWallet> findByTransactionTransactionId(UUID transactionId);
    
    /**
     * Get total balance for a domain expert (sum of all pending amounts)
     */
    @Query("SELECT COALESCE(SUM(sw.amount), 0) FROM SystemWallet sw WHERE sw.belongsTo.id = :domainExpertId AND sw.status = 'PENDING'")
    Integer getTotalBalance(@Param("domainExpertId") UUID domainExpertId);
    
    /**
     * Get all pending wallet entries for a domain expert
     */
    @Query("SELECT sw FROM SystemWallet sw WHERE sw.belongsTo.id = :domainExpertId AND sw.status = 'PENDING' ORDER BY sw.createdAt DESC")
    List<SystemWallet> getPendingWalletEntries(@Param("domainExpertId") UUID domainExpertId);
    
    /**
     * Get all withdrawn wallet entries for a domain expert
     */
    @Query("SELECT sw FROM SystemWallet sw WHERE sw.belongsTo.id = :domainExpertId AND sw.status = 'WITHDRAWN' ORDER BY sw.withdrawnAt DESC")
    Page<SystemWallet> getWithdrawnWalletEntries(@Param("domainExpertId") UUID domainExpertId, Pageable pageable);
    
    /**
     * Get wallet entries by status
     */
    Page<SystemWallet> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
}
