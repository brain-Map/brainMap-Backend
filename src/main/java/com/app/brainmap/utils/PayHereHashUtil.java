package com.app.brainmap.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
public class PayHereHashUtil {
    
    /**
     * Generate PayHere MD5 hash for payment session
     * Hash format: merchant_id + order_id + amount + currency + md5(merchant_secret)
     */
    public static String generatePaymentHash(String merchantId, String orderId, 
                                           BigDecimal amount, String currency, String merchantSecret) {
        try {
            String merchantSecretHash = DigestUtils.md5Hex(merchantSecret).toUpperCase();
            String hashString = merchantId + orderId + amount.toString() + currency + merchantSecretHash;
            String hash = DigestUtils.md5Hex(hashString).toUpperCase();
            
            log.debug("Generated PayHere hash for orderId: {}, hash: {}", orderId, hash);
            return hash;
        } catch (Exception e) {
            log.error("Error generating PayHere hash for orderId: {}", orderId, e);
            throw new RuntimeException("Failed to generate PayHere hash", e);
        }
    }
    
    /**
     * Verify PayHere callback signature
     * Hash format: merchant_id + order_id + payhere_amount + payhere_currency + status_code + md5(merchant_secret)
     */
    public static boolean verifyCallbackSignature(Map<String, String> callbackData, String merchantSecret) {
        try {
            String merchantId = callbackData.get("merchant_id");
            String orderId = callbackData.get("order_id");
            String amount = callbackData.get("payhere_amount");
            String currency = callbackData.get("payhere_currency");
            String statusCode = callbackData.get("status_code");
            String receivedHash = callbackData.get("md5sig");
            
            if (merchantId == null || orderId == null || amount == null || 
                currency == null || statusCode == null || receivedHash == null) {
                log.warn("Missing required callback parameters for signature verification");
                return false;
            }
            
            String merchantSecretHash = DigestUtils.md5Hex(merchantSecret).toUpperCase();
            String hashString = merchantId + orderId + amount + currency + statusCode + merchantSecretHash;
            String calculatedHash = DigestUtils.md5Hex(hashString).toUpperCase();
            
            boolean isValid = calculatedHash.equals(receivedHash);
            
            if (isValid) {
                log.debug("PayHere callback signature verified successfully for orderId: {}", orderId);
            } else {
                log.warn("PayHere callback signature verification failed for orderId: {}. Expected: {}, Received: {}", 
                        orderId, calculatedHash, receivedHash);
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("Error verifying PayHere callback signature", e);
            return false;
        }
    }
    
    /**
     * Generate a unique payment ID
     */
    public static String generatePaymentId() {
        return "PAY_" + System.currentTimeMillis() + "_" + Math.random();
    }
    
    /**
     * Generate a unique order ID if not provided
     */
    public static String generateOrderId() {
        return "ORDER_" + System.currentTimeMillis();
    }
    
    /**
     * Validate PayHere status codes
     */
    public static boolean isSuccessStatus(String statusCode) {
        return "2".equals(statusCode); // PayHere success status code
    }
    
    public static boolean isFailedStatus(String statusCode) {
        return "-1".equals(statusCode) || "-2".equals(statusCode) || "-3".equals(statusCode);
    }
    
    public static boolean isPendingStatus(String statusCode) {
        return "0".equals(statusCode) || "1".equals(statusCode);
    }
    
    /**
     * Get payment status description from PayHere status code
     */
    public static String getStatusDescription(String statusCode) {
        return switch (statusCode) {
            case "2" -> "Payment completed successfully";
            case "1" -> "Payment pending";
            case "0" -> "Payment initiated";
            case "-1" -> "Payment cancelled by customer";
            case "-2" -> "Payment failed";
            case "-3" -> "Payment chargeback";
            default -> "Unknown status: " + statusCode;
        };
    }
}
