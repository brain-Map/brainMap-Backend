package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.PaymentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentStatusHistoryRepository extends JpaRepository<PaymentStatusHistory, Long> {
    
    @Query("SELECT psh FROM PaymentStatusHistory psh WHERE psh.paymentSession.id = :paymentSessionId ORDER BY psh.createdAt DESC")
    List<PaymentStatusHistory> findByPaymentSessionIdOrderByCreatedAtDesc(@Param("paymentSessionId") Long paymentSessionId);
    
    @Query("SELECT psh FROM PaymentStatusHistory psh WHERE psh.paymentSession.paymentId = :paymentId ORDER BY psh.createdAt DESC")
    List<PaymentStatusHistory> findByPaymentIdOrderByCreatedAtDesc(@Param("paymentId") String paymentId);
}
