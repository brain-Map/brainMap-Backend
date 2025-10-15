package com.app.brainmap.repositories;

import com.app.brainmap.domain.PaymentStatus;
import com.app.brainmap.domain.entities.PaymentSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentSessionRepository extends JpaRepository<PaymentSession, Long> {
    
    Optional<PaymentSession> findByPaymentId(String paymentId);
    
    Optional<PaymentSession> findByOrderId(String orderId);
    
    Page<PaymentSession> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    @Query("SELECT ps FROM PaymentSession ps WHERE ps.user.id = :userId AND ps.status = :status ORDER BY ps.createdAt DESC")
    Page<PaymentSession> findByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") PaymentStatus status, Pageable pageable);
    
    @Query("SELECT COUNT(ps) FROM PaymentSession ps WHERE ps.status = :status")
    long countByStatus(@Param("status") PaymentStatus status);
    
    @Query("SELECT COUNT(ps) FROM PaymentSession ps WHERE ps.user.id = :userId AND ps.status = :status")
    long countByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") PaymentStatus status);
    
    @Query("SELECT ps FROM PaymentSession ps WHERE ps.status = :status AND ps.createdAt < :expiredDate")
    Page<PaymentSession> findExpiredPendingPayments(@Param("status") PaymentStatus status, @Param("expiredDate") java.time.LocalDateTime expiredDate, Pageable pageable);
}
