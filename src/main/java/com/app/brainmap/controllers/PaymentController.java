package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.payment.PaymentHistoryResponse;
import com.app.brainmap.domain.dto.payment.PaymentSessionRequest;
import com.app.brainmap.domain.dto.payment.PaymentSessionResponse;
import com.app.brainmap.domain.dto.payment.PaymentStatusResponse;
import com.app.brainmap.exceptions.PaymentException;
import com.app.brainmap.exceptions.PaymentNotFoundException;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.PayHereService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Management", description = "PayHere payment gateway integration APIs")
public class PaymentController {
    
    private final PayHereService payHereService;
    
    @PostMapping("/create-session")
    @Operation(summary = "Create Payment Session", description = "Create a new PayHere payment session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment session created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentSessionResponse> createPaymentSession(
            @Valid @RequestBody PaymentSessionRequest request,
            Authentication authentication) {
        
        try {
            UUID userId = getCurrentUserId(authentication);
            log.info("Creating payment session for user: {}, orderId: {}", userId, request.getOrderId());
            
            PaymentSessionResponse response = payHereService.createPaymentSession(request, userId);
            
            return ResponseEntity.ok(response);
        } catch (PaymentException e) {
            log.error("Payment creation failed", e);
            return ResponseEntity.badRequest().body(
                PaymentSessionResponse.builder()
                    .message("Failed to create payment session: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            log.error("Unexpected error creating payment session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                PaymentSessionResponse.builder()
                    .message("An unexpected error occurred")
                    .build()
            );
        }
    }
    
    @GetMapping("/status/{paymentId}")
    @Operation(summary = "Get Payment Status", description = "Get payment status by payment ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment status retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(
            @Parameter(description = "Payment ID") @PathVariable String paymentId,
            Authentication authentication) {
        
        try {
            getCurrentUserId(authentication); // Ensure user is authenticated
            PaymentStatusResponse response = payHereService.getPaymentStatus(paymentId);
            return ResponseEntity.ok(response);
        } catch (PaymentNotFoundException e) {
            log.warn("Payment not found: {}", paymentId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving payment status: {}", paymentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/status/order/{orderId}")
    @Operation(summary = "Get Payment Status by Order ID", description = "Get payment status by order ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment status retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PaymentStatusResponse> getPaymentStatusByOrderId(
            @Parameter(description = "Order ID") @PathVariable String orderId,
            Authentication authentication) {
        
        try {
            getCurrentUserId(authentication); // Ensure user is authenticated
            PaymentStatusResponse response = payHereService.getPaymentStatusByOrderId(orderId);
            return ResponseEntity.ok(response);
        } catch (PaymentNotFoundException e) {
            log.warn("Payment not found for order: {}", orderId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving payment status for order: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/payhere/callback")
    @Operation(summary = "PayHere Callback Handler", description = "Handle PayHere payment callbacks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Callback processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid callback data"),
        @ApiResponse(responseCode = "500", description = "Callback processing failed")
    })
    public ResponseEntity<String> handlePayHereCallback(
            @RequestParam Map<String, String> callbackData,
            HttpServletRequest request) {
        
        try {
            log.info("Received PayHere callback from IP: {}, orderId: {}", 
                    getClientIpAddress(request), callbackData.get("order_id"));
            
            payHereService.processPayHereCallback(callbackData);
            
            log.info("PayHere callback processed successfully for order: {}", callbackData.get("order_id"));
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Failed to process PayHere callback for order: {}", callbackData.get("order_id"), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }
    
    @PostMapping("/cancel/{paymentId}")
    @Operation(summary = "Cancel Payment", description = "Cancel a pending payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot cancel payment"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Map<String, String>> cancelPayment(
            @Parameter(description = "Payment ID") @PathVariable String paymentId,
            Authentication authentication) {
        
        try {
            UUID userId = getCurrentUserId(authentication);
            payHereService.cancelPayment(paymentId, userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Payment cancelled successfully",
                "paymentId", paymentId
            ));
        } catch (PaymentNotFoundException e) {
            log.warn("Payment not found for cancellation: {}", paymentId);
            return ResponseEntity.notFound().build();
        } catch (PaymentException e) {
            log.warn("Cannot cancel payment: {}", paymentId, e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "paymentId", paymentId
            ));
        } catch (Exception e) {
            log.error("Error cancelling payment: {}", paymentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "An unexpected error occurred",
                "paymentId", paymentId
            ));
        }
    }
    
    @GetMapping("/history")
    @Operation(summary = "Get Payment History", description = "Get user's payment history with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment history retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<PaymentHistoryResponse>> getPaymentHistory(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        try {
            UUID userId = getCurrentUserId(authentication);
            Pageable pageable = PageRequest.of(page, size);
            Page<PaymentHistoryResponse> history = payHereService.getPaymentHistory(userId, pageable);
            
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error retrieving payment history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Admin endpoints (optional - for admin dashboard)
    @GetMapping("/admin/all")
    @Operation(summary = "Get All Payments (Admin)", description = "Get all payments for admin dashboard")
    public ResponseEntity<String> getAllPayments(Authentication authentication) {
        // TODO: Implement admin functionality if needed
        return ResponseEntity.ok("Admin endpoint - to be implemented");
    }
    
    @PostMapping("/admin/{paymentId}/refund")
    @Operation(summary = "Refund Payment (Admin)", description = "Process payment refund")
    public ResponseEntity<String> refundPayment(
            @PathVariable String paymentId,
            Authentication authentication) {
        // TODO: Implement refund functionality if needed
        return ResponseEntity.ok("Refund endpoint - to be implemented");
    }
    
    // Utility methods
    private UUID getCurrentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserDetails)) {
            throw new PaymentException("User authentication required");
        }
        
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}
