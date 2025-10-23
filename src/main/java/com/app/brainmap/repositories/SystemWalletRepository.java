package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.SystemWallet;
import com.app.brainmap.domain.dto.wallet.SystemWalletTotalsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SystemWalletRepository extends JpaRepository<SystemWallet, UUID> {
    Optional<SystemWallet> findByBelongsToId(UUID domainExpertId);
    Page<SystemWallet> findAllByOrderByUpdatedAtDesc(Pageable pageable);
    Page<SystemWallet> findByStatusOrderByUpdatedAtDesc(String status, Pageable pageable);
    boolean existsByBelongsToId(UUID domainExpertId);

    @Query("SELECT new com.app.brainmap.domain.dto.wallet.SystemWalletTotalsResponse("
        + "COALESCE(SUM(sw.holdAmount), 0L), "
        + "COALESCE(SUM(sw.releasedAmount), 0L), "
        + "COALESCE(SUM(sw.systemCharged), 0L), "
        + "COALESCE(SUM(sw.withdrawnAmount), 0L)) "
        + "FROM SystemWallet sw")
    SystemWalletTotalsResponse getTotals();
}
