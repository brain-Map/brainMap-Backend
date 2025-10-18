package com.app.brainmap.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Base64;

@Slf4j
public class PayHereHashUtil {
    
    /**
     * Generate PayHere MD5 hash for payment session
     * Hash format: merchant_id + order_id + amount + currency + md5(merchant_secret)
     * Amount must be formatted with 2 decimal places (e.g., 1000.00)
     */
    public static String generatePaymentHash(String merchantId, String orderId, 
                                           BigDecimal amount, String currency, String merchantSecret) {
        try {
            // Format amount with exactly 2 decimal places (PayHere requirement)
            String amountFormatted = String.format("%.2f", amount);
            
            // If merchantSecret was accidentally stored base64-encoded (common mistake),
            // decode it before hashing. This keeps behavior backward-compatible.
            String secretToUse = maybeDecodeBase64(merchantSecret);

            // Generate MD5 hash of merchant secret and convert to UPPERCASE
            String merchantSecretHash = DigestUtils.md5Hex(secretToUse).toUpperCase();
            
            // Build hash string: merchant_id + order_id + amount + currency + md5(merchant_secret)
            String hashString = merchantId + orderId + amountFormatted + currency + merchantSecretHash;
            
            // Generate final MD5 hash and convert to UPPERCASE
            String hash = DigestUtils.md5Hex(hashString).toUpperCase();
            
            log.info("🔐 PayHere Hash Generation:");
            log.info("   Merchant ID: {}", merchantId);
            log.info("   Order ID: {}", orderId);
            log.info("   Amount: {} (formatted from {})", amountFormatted, amount);
            log.info("   Currency: {}", currency);
            log.info("   Merchant Secret: {}... (first 10 chars)", merchantSecret.substring(0, Math.min(10, merchantSecret.length())));
            log.info("   Merchant Secret Hash: {}", merchantSecretHash);
            log.info("   Hash String: {}", hashString);
            log.info("   Final Hash: {}", hash);
            
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
            
            // Same base64-decoding tolerance as in generatePaymentHash
            String secretToUse = maybeDecodeBase64(merchantSecret);
            String merchantSecretHash = DigestUtils.md5Hex(secretToUse).toUpperCase();
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
     * If the provided string looks like Base64, attempt to decode it and return
     * the decoded value. If decoding fails or the decoded value is identical to
     * the input (or non-printable), return the original input.
     */
    private static String maybeDecodeBase64(String s) {
        if (s == null) return null;
        // Heuristic: Base64 strings commonly contain '=' padding or only base64 chars
        if (!s.matches("^[A-Za-z0-9+/=\\r\\n]+$")) return s;
        try {
            byte[] decoded = Base64.getDecoder().decode(s.trim());
            String decodedStr = new String(decoded);
            // If decoded looks printable and different, return it
            if (!decodedStr.equals(s) && decodedStr.chars().allMatch(c -> c >= 32 && c <= 126)) {
                log.debug("Detected Base64-encoded merchant secret; using decoded value for hashing");
                return decodedStr;
            }
        } catch (IllegalArgumentException ex) {
            // Not valid Base64, fall through
        }
        return s;
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
