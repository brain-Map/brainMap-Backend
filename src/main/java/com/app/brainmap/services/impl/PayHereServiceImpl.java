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
            log.info("💳 Creating payment session for user: {}, orderId: {}, amount: {}", 
                    userId != null ? userId : "ANONYMOUS", request.getOrderId(), request.getAmount());
            
            // Get user if userId is provided (null for anonymous payments)
            User user = null;
            if (userId != null) {
                try {
                    user = userService.getUserById(userId);
                } catch (Exception e) {
                    log.warn("⚠️ Could not find user {}, proceeding with anonymous payment", userId);
                }
            }
            
            // Generate unique payment ID
            String paymentId = PayHereHashUtil.generatePaymentId();
            
            // Use provided order ID or generate one
            String orderId = request.getOrderId() != null ? request.getOrderId() : PayHereHashUtil.generateOrderId();
            
            log.info("🔑 Generated paymentId: {}, orderId: {}", paymentId, orderId);
            
            // Combine firstName and lastName for customerName
            String customerName = (request.getFirstName() + " " + request.getLastName()).trim();
            
            // Use default item description if not provided
            String itemDescription = request.getItemDescription() != null && !request.getItemDescription().isBlank() 
                    ? request.getItemDescription() 
                    : "Payment for order " + orderId;
            
            log.info("👤 Customer: {}, 📧 Email: {}, 💰 Amount: {} {}", 
                    customerName, request.getEmail(), request.getAmount(), request.getCurrency());
            
            // Create payment session
            PaymentSession paymentSession = PaymentSession.builder()
                    .paymentId(paymentId)
                    .orderId(orderId)
                    .user(user)  // Can be null for anonymous payments
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .itemDescription(itemDescription)
                    .customerName(customerName)
                    .customerEmail(request.getEmail())
                    .customerPhone(request.getPhone())
                    .customerAddress(request.getAddress())
                    .city(request.getCity())
                    .country(request.getCountry() != null ? request.getCountry() : "Sri Lanka")
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
            
            // Generate redirect URL to our auto-submit form endpoint
            String redirectUrl = appConfig.getBackendUrl() + "/api/payments/payhere/redirect/" + paymentId;
            
            log.info("✅ Payment session created successfully: {}", paymentId);
            log.info("🎯 Redirect URL (auto-submit form): {}", redirectUrl);
            
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
    
    @Override
    public String generatePayHereForm(String paymentId) {
        PaymentSession paymentSession = paymentSessionRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));
        
        String payhereUrl = payHereConfig.getPayHereUrl();
        String returnUrl = getReturnUrl();
        String cancelUrl = getCancelUrl();
        String notifyUrl = getNotifyUrl();
        
        log.info("🎨 Generating PayHere form for payment: {}", paymentId);
        log.info("   📍 PayHere URL: {}", payhereUrl);
        log.info("   🔐 Form Data:");
        log.info("      merchant_id: {}", paymentSession.getPayHereMerchantId());
        log.info("      order_id: {}", paymentSession.getOrderId());
        log.info("      amount: {}", String.format("%.2f", paymentSession.getAmount()));
        log.info("      currency: {}", paymentSession.getCurrency());
        log.info("      hash: {}", paymentSession.getPayHereHash());
        log.info("      return_url: {}", returnUrl);
        log.info("      cancel_url: {}", cancelUrl);
        log.info("      notify_url: {}", notifyUrl);
        log.info("      items: {}", paymentSession.getItemDescription());
        log.info("      first_name: {}", getFirstName(paymentSession.getCustomerName()));
        log.info("      last_name: {}", getLastName(paymentSession.getCustomerName()));
        log.info("      email: {}", paymentSession.getCustomerEmail());
        log.info("      phone: {}", paymentSession.getCustomerPhone());
        log.info("      address: {}", paymentSession.getCustomerAddress());
        log.info("      city: {}", paymentSession.getCity());
        log.info("      country: {}", paymentSession.getCountry());
        
        // Build HTML form that auto-submits to PayHere
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <title>Redirecting to PayHere...</title>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }\n");
        html.append("        .loader { border: 5px solid #f3f3f3; border-top: 5px solid #3498db; border-radius: 50%; width: 50px; height: 50px; animation: spin 1s linear infinite; margin: 20px auto; }\n");
        html.append("        @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <h2>Redirecting to PayHere Payment Gateway...</h2>\n");
        html.append("    <div class=\"loader\"></div>\n");
        html.append("    <p>Please wait while we redirect you to the payment page.</p>\n");
        html.append("    <form id=\"payhere_form\" method=\"POST\" action=\"").append(payhereUrl).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"merchant_id\" value=\"").append(paymentSession.getPayHereMerchantId()).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"return_url\" value=\"").append(returnUrl).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"cancel_url\" value=\"").append(cancelUrl).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"notify_url\" value=\"").append(notifyUrl).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"order_id\" value=\"").append(paymentSession.getOrderId()).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"items\" value=\"").append(escapeHtml(paymentSession.getItemDescription())).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"currency\" value=\"").append(paymentSession.getCurrency()).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"amount\" value=\"").append(String.format("%.2f", paymentSession.getAmount())).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"first_name\" value=\"").append(escapeHtml(getFirstName(paymentSession.getCustomerName()))).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"last_name\" value=\"").append(escapeHtml(getLastName(paymentSession.getCustomerName()))).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"email\" value=\"").append(escapeHtml(paymentSession.getCustomerEmail())).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"phone\" value=\"").append(escapeHtml(paymentSession.getCustomerPhone() != null ? paymentSession.getCustomerPhone() : "")).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"address\" value=\"").append(escapeHtml(paymentSession.getCustomerAddress() != null ? paymentSession.getCustomerAddress() : "")).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"city\" value=\"").append(escapeHtml(paymentSession.getCity())).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"country\" value=\"").append(escapeHtml(paymentSession.getCountry())).append("\">\n");
        html.append("        <input type=\"hidden\" name=\"hash\" value=\"").append(paymentSession.getPayHereHash()).append("\">\n");
        html.append("    </form>\n");
        html.append("    <script>\n");
        html.append("        document.getElementById('payhere_form').submit();\n");
        html.append("    </script>\n");
        html.append("</body>\n");
        html.append("</html>");
        
        log.info("✅ PayHere form generated successfully");
        
        return html.toString();
    }
    
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    private String buildPayHereRedirectUrl(PaymentSession paymentSession, String hash) {
        String baseUrl = payHereConfig.getPayHereUrl();
        String returnUrl = getReturnUrl();
        String cancelUrl = getCancelUrl();
        String notifyUrl = getNotifyUrl();
        
        log.info("🏗️ Building PayHere redirect URL:");
        log.info("   📍 Base URL: {}", baseUrl);
        log.info("   🔙 Return URL: {}", returnUrl);
        log.info("   ❌ Cancel URL: {}", cancelUrl);
        log.info("   📨 Notify URL: {}", notifyUrl);
        log.info("   🆔 Merchant ID: {}", paymentSession.getPayHereMerchantId());
        log.info("   📦 Order ID: {}", paymentSession.getOrderId());
        log.info("   💰 Amount: {}", paymentSession.getAmount());
        log.info("   💱 Currency: {}", paymentSession.getCurrency());
        
        String redirectUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("merchant_id", paymentSession.getPayHereMerchantId())
                .queryParam("return_url", returnUrl)
                .queryParam("cancel_url", cancelUrl)
                .queryParam("notify_url", notifyUrl)
                .queryParam("order_id", paymentSession.getOrderId())
                .queryParam("items", paymentSession.getItemDescription())
                .queryParam("currency", paymentSession.getCurrency())
                .queryParam("amount", String.format("%.2f", paymentSession.getAmount()))
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
        
        log.info("🔗 Generated PayHere Redirect URL: {}", redirectUrl);
        log.info("🔗 URL Length: {} characters", redirectUrl.length());
        
        return redirectUrl;
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
