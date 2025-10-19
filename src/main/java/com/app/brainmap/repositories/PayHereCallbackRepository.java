package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.PayHereCallback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayHereCallbackRepository extends JpaRepository<PayHereCallback, UUID> {

    List<PayHereCallback> findByOrderIdOrderByCreatedAtDesc(String orderId);
    
    Optional<PayHereCallback> findByPaymentId(String paymentId);
    
    @Query("SELECT phc FROM PayHereCallback phc WHERE phc.processed = false ORDER BY phc.createdAt ASC")
    List<PayHereCallback> findUnprocessedCallbacks();
    
    @Query("SELECT phc FROM PayHereCallback phc WHERE phc.paymentSession.paymentId = :paymentSessionId ORDER BY phc.createdAt DESC")
    List<PayHereCallback> findByPaymentSessionIdOrderByCreatedAtDesc(@Param("paymentSessionId") String paymentSessionId);
}
