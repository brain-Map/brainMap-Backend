package com.app.brainmap.services;

import com.app.brainmap.domain.dto.payment.PaymentHistoryResponse;
import com.app.brainmap.domain.dto.payment.PaymentSessionRequest;
import com.app.brainmap.domain.dto.payment.PaymentSessionResponse;
import com.app.brainmap.domain.dto.payment.PaymentStatusResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

public interface PayHereService {
    
    /**
     * Create a new payment session
     */
    PaymentSessionResponse createPaymentSession(PaymentSessionRequest request, UUID userId);
    
    /**
     * Get payment status by payment ID
     */
    PaymentStatusResponse getPaymentStatus(String paymentId);
    
    /**
     * Get payment status by order ID
     */
    PaymentStatusResponse getPaymentStatusByOrderId(String orderId);
    
    /**
     * Process PayHere callback
     */
    void processPayHereCallback(Map<String, String> callbackData);
    
    /**
     * Cancel a payment
     */
    void cancelPayment(String paymentId, UUID userId);
    
    /**
     * Get payment history for a user
     */
    Page<PaymentHistoryResponse> getPaymentHistory(UUID userId, Pageable pageable);
    
    /**
     * Verify PayHere callback signature
     */
    boolean verifyCallbackSignature(Map<String, String> callbackData);
}
