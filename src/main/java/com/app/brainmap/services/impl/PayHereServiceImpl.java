package com.app.brainmap.services.impl;

import com.app.brainmap.config.AppConfig;
import com.app.brainmap.config.PayHereConfig;
import com.app.brainmap.domain.PaymentStatus;
import com.app.brainmap.domain.dto.payment.*;
import com.app.brainmap.domain.entities.*;
import com.app.brainmap.exceptions.PayHereSignatureException;
import com.app.brainmap.exceptions.PaymentException;
import com.app.brainmap.exceptions.PaymentNotFoundException;
import com.app.brainmap.repositories.*;
import com.app.brainmap.services.PayHereService;
import com.app.brainmap.services.UserService;
import com.app.brainmap.utils.PayHereHashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PayHereServiceImpl implements PayHereService {
    
    private final PaymentSessionRepository paymentSessionRepository;
    private final PaymentStatusHistoryRepository paymentStatusHistoryRepository;
    private final PayHereCallbackRepository payHereCallbackRepository;
    private final UserService userService;
    private final PayHereConfig payHereConfig;
    private final AppConfig appConfig;
    
    @Override
    public PaymentSessionResponse createPaymentSession(PaymentSessionRequest request, UUID userId) {
        try {
            log.info("Creating payment session for user: {}, orderId: {}, amount: {}", 
                    userId, request.getOrderId(), request.getAmount());
            
            // Get user
            User user = userService.getUserById(userId);
            
            // Generate unique payment ID
            String paymentId = PayHereHashUtil.generatePaymentId();
            
            // Use provided order ID or generate one
            String orderId = request.getOrderId() != null ? request.getOrderId() : PayHereHashUtil.generateOrderId();
            
            // Create payment session
            PaymentSession paymentSession = PaymentSession.builder()
                    .paymentId(paymentId)
                    .orderId(orderId)
                    .user(user)
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .itemDescription(request.getItemDescription())
                    .customerName(request.getCustomerName())
                    .customerEmail(request.getCustomerEmail())
                    .customerPhone(request.getCustomerPhone())
                    .customerAddress(request.getCustomerAddress())
                    .city(request.getCity())
                    .country(request.getCountry())
                    .payHereMode(payHereConfig.getMode())
                    .payHereMerchantId(payHereConfig.getMerchantId())
                    .status(PaymentStatus.PENDING)
                    .build();
            
            // Generate PayHere hash
            String hash = PayHereHashUtil.generatePaymentHash(
                    payHereConfig.getMerchantId(),
                    orderId,
                    request.getAmount(),
                    request.getCurrency(),
                    payHereConfig.getMerchantSecret()
            );
            paymentSession.setPayHereHash(hash);
            
            // Save payment session
            paymentSession = paymentSessionRepository.save(paymentSession);
            
            // Create status history
            createStatusHistory(paymentSession, null, PaymentStatus.PENDING.name(), 
                              "SYSTEM", "Payment session created", null);
            
            // Generate PayHere redirect URL
            String redirectUrl = buildPayHereRedirectUrl(paymentSession, hash);
            
            log.info("Payment session created successfully: {}", paymentId);
            
            return PaymentSessionResponse.builder()
                    .paymentId(paymentId)
                    .redirectUrl(redirectUrl)
                    .orderId(orderId)
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .status(PaymentStatus.PENDING.name())
                    .message("Payment session created successfully")
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to create payment session for user: {}", userId, e);
            throw new PaymentException("Failed to create payment session: " + e.getMessage(), e);
        }
    }
    
    @Override
    public PaymentStatusResponse getPaymentStatus(String paymentId) {
        PaymentSession paymentSession = paymentSessionRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));
        
        return buildPaymentStatusResponse(paymentSession);
    }
    
    @Override
    public PaymentStatusResponse getPaymentStatusByOrderId(String orderId) {
        PaymentSession paymentSession = paymentSessionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderId));
        
        return buildPaymentStatusResponse(paymentSession);
    }
    
    @Override
    public void processPayHereCallback(Map<String, String> callbackData) {
        try {
            log.info("Processing PayHere callback for order: {}", callbackData.get("order_id"));
            
            // Save callback data first
            PayHereCallback callback = saveCallbackData(callbackData);
            
            try {
                // Verify signature
                if (!verifyCallbackSignature(callbackData)) {
                    callback.setProcessingError("Invalid signature");
                    payHereCallbackRepository.save(callback);
                    throw new PayHereSignatureException("Invalid PayHere callback signature");
                }
                
                // Find payment session
                String orderId = callbackData.get("order_id");
                PaymentSession paymentSession = paymentSessionRepository.findByOrderId(orderId)
                        .orElseThrow(() -> new PaymentNotFoundException("Payment session not found for order: " + orderId));
                
                callback.setPaymentSession(paymentSession);
                
                // Update payment status based on PayHere response
                updatePaymentStatus(paymentSession, callbackData);
                
                // Mark callback as processed
                callback.setProcessed(true);
                payHereCallbackRepository.save(callback);
                
                log.info("PayHere callback processed successfully for order: {}", orderId);
                
            } catch (Exception e) {
                callback.setProcessed(false);
                callback.setProcessingError(e.getMessage());
                payHereCallbackRepository.save(callback);
                throw e;
            }
            
        } catch (Exception e) {
            log.error("Failed to process PayHere callback", e);
            throw new PaymentException("Failed to process PayHere callback: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void cancelPayment(String paymentId, UUID userId) {
        PaymentSession paymentSession = paymentSessionRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));
        
        // Verify user ownership
        if (!paymentSession.getUser().getId().equals(userId)) {
            throw new PaymentException("Unauthorized to cancel this payment");
        }
        
        // Only allow cancellation of pending payments
        if (paymentSession.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentException("Cannot cancel payment in status: " + paymentSession.getStatus());
        }
        
        // Update status to cancelled
        PaymentStatus oldStatus = paymentSession.getStatus();
        paymentSession.setStatus(PaymentStatus.CANCELLED);
        paymentSession.setUpdatedAt(LocalDateTime.now());
        paymentSessionRepository.save(paymentSession);
        
        // Create status history
        createStatusHistory(paymentSession, oldStatus.name(), PaymentStatus.CANCELLED.name(), 
                          "USER", "Payment cancelled by user", null);
        
        log.info("Payment cancelled: {}", paymentId);
    }
    
    @Override
    public Page<PaymentHistoryResponse> getPaymentHistory(UUID userId, Pageable pageable) {
        Page<PaymentSession> paymentSessions = paymentSessionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        return paymentSessions.map(this::buildPaymentHistoryResponse);
    }
    
    @Override
    public boolean verifyCallbackSignature(Map<String, String> callbackData) {
        return PayHereHashUtil.verifyCallbackSignature(callbackData, payHereConfig.getMerchantSecret());
    }
    
    private String buildPayHereRedirectUrl(PaymentSession paymentSession, String hash) {
        return UriComponentsBuilder.fromHttpUrl(payHereConfig.getPayHereUrl())
                .queryParam("merchant_id", paymentSession.getPayHereMerchantId())
                .queryParam("return_url", getReturnUrl())
                .queryParam("cancel_url", getCancelUrl())
                .queryParam("notify_url", getNotifyUrl())
                .queryParam("order_id", paymentSession.getOrderId())
                .queryParam("items", paymentSession.getItemDescription())
                .queryParam("currency", paymentSession.getCurrency())
                .queryParam("amount", paymentSession.getAmount().toString())
                .queryParam("first_name", getFirstName(paymentSession.getCustomerName()))
                .queryParam("last_name", getLastName(paymentSession.getCustomerName()))
                .queryParam("email", paymentSession.getCustomerEmail())
                .queryParam("phone", paymentSession.getCustomerPhone() != null ? paymentSession.getCustomerPhone() : "")
                .queryParam("address", paymentSession.getCustomerAddress() != null ? paymentSession.getCustomerAddress() : "")
                .queryParam("city", paymentSession.getCity())
                .queryParam("country", paymentSession.getCountry())
                .queryParam("hash", hash)
                .build()
                .toUriString();
    }
    
    private void updatePaymentStatus(PaymentSession paymentSession, Map<String, String> callbackData) {
        String statusCode = callbackData.get("status_code");
        PaymentStatus oldStatus = paymentSession.getStatus();
        PaymentStatus newStatus;
        
        // Map PayHere status codes to our status enum
        if (PayHereHashUtil.isSuccessStatus(statusCode)) {
            newStatus = PaymentStatus.SUCCESS;
            paymentSession.setPaymentDate(LocalDateTime.now());
        } else if (PayHereHashUtil.isFailedStatus(statusCode)) {
            newStatus = PaymentStatus.FAILED;
        } else if (PayHereHashUtil.isPendingStatus(statusCode)) {
            newStatus = PaymentStatus.PENDING;
        } else {
            log.warn("Unknown PayHere status code: {}", statusCode);
            newStatus = PaymentStatus.FAILED;
        }
        
        // Update payment session
        paymentSession.setStatus(newStatus);
        paymentSession.setTransactionId(callbackData.get("payment_id"));
        paymentSession.setPayHereOrderId(callbackData.get("payhere_order_id"));
        paymentSession.setPaymentMethod(callbackData.get("method"));
        paymentSession.setUpdatedAt(LocalDateTime.now());
        
        paymentSessionRepository.save(paymentSession);
        
        // Create status history
        Map<String, Object> payHereData = new HashMap<>(callbackData);
        createStatusHistory(paymentSession, oldStatus.name(), newStatus.name(), 
                          "PAYHERE", PayHereHashUtil.getStatusDescription(statusCode), payHereData);
        
        log.info("Payment status updated: {} -> {} for payment: {}", 
                oldStatus, newStatus, paymentSession.getPaymentId());
    }
    
    private PayHereCallback saveCallbackData(Map<String, String> callbackData) {
        PayHereCallback callback = PayHereCallback.builder()
                .orderId(callbackData.get("order_id"))
                .payHereOrderId(callbackData.get("payhere_order_id"))
                .merchantId(callbackData.get("merchant_id"))
                .paymentId(callbackData.get("payment_id"))
                .statusCode(callbackData.get("status_code"))
                .md5Sig(callbackData.get("md5sig"))
                .method(callbackData.get("method"))
                .rawData(callbackData)
                .processed(false)
                .build();
        
        // Parse amount and currency if available
        String amountStr = callbackData.get("payhere_amount");
        if (amountStr != null) {
            try {
                callback.setAmount(new BigDecimal(amountStr));
            } catch (NumberFormatException e) {
                log.warn("Invalid amount in callback: {}", amountStr);
            }
        }
        
        callback.setCurrency(callbackData.get("payhere_currency"));
        
        return payHereCallbackRepository.save(callback);
    }
    
    private void createStatusHistory(PaymentSession paymentSession, String previousStatus, 
                                   String newStatus, String changedBy, String reason, 
                                   Map<String, Object> payHereData) {
        PaymentStatusHistory history = PaymentStatusHistory.builder()
                .paymentSession(paymentSession)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .changeReason(reason)
                .payHereData(payHereData)
                .build();
        
        paymentStatusHistoryRepository.save(history);
    }
    
    private PaymentStatusResponse buildPaymentStatusResponse(PaymentSession paymentSession) {
        return PaymentStatusResponse.builder()
                .paymentId(paymentSession.getPaymentId())
                .orderId(paymentSession.getOrderId())
                .status(paymentSession.getStatus())
                .amount(paymentSession.getAmount())
                .currency(paymentSession.getCurrency())
                .transactionId(paymentSession.getTransactionId())
                .paymentMethod(paymentSession.getPaymentMethod())
                .paymentDate(paymentSession.getPaymentDate())
                .customerName(paymentSession.getCustomerName())
                .customerEmail(paymentSession.getCustomerEmail())
                .message(getStatusMessage(paymentSession.getStatus()))
                .build();
    }
    
    private PaymentHistoryResponse buildPaymentHistoryResponse(PaymentSession paymentSession) {
        return PaymentHistoryResponse.builder()
                .paymentId(paymentSession.getPaymentId())
                .orderId(paymentSession.getOrderId())
                .status(paymentSession.getStatus())
                .amount(paymentSession.getAmount())
                .currency(paymentSession.getCurrency())
                .itemDescription(paymentSession.getItemDescription())
                .transactionId(paymentSession.getTransactionId())
                .paymentMethod(paymentSession.getPaymentMethod())
                .createdAt(paymentSession.getCreatedAt())
                .paymentDate(paymentSession.getPaymentDate())
                .build();
    }
    
    private String getStatusMessage(PaymentStatus status) {
        return switch (status) {
            case PENDING -> "Payment is pending";
            case SUCCESS -> "Payment completed successfully";
            case FAILED -> "Payment failed";
            case CANCELLED -> "Payment was cancelled";
            case REFUNDED -> "Payment was refunded";
        };
    }
    
    private String getReturnUrl() {
        return appConfig.getFrontendUrl() + "/payment-gateway/success";
    }
    
    private String getCancelUrl() {
        return appConfig.getFrontendUrl() + "/payment-gateway/cancel";
    }
    
    private String getNotifyUrl() {
        return appConfig.getBackendUrl() + "/api/payments/payhere/callback";
    }
    
    private String getFirstName(String fullName) {
        if (fullName == null) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }
    
    private String getLastName(String fullName) {
        if (fullName == null) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length)) : "";
    }
}
