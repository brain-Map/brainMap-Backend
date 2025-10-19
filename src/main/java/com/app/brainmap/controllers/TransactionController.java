package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.transaction.TransactionDetailsDto;
import com.app.brainmap.domain.dto.transaction.TransactionRequest;
import com.app.brainmap.domain.dto.transaction.TransactionResponse;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction Management", description = "APIs for managing wallet transactions")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping("/record")
    @Operation(summary = "Record Transaction", description = "Create a new transaction record after payment completion")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT authentication required"),
        @ApiResponse(responseCode = "404", description = "Sender or receiver not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TransactionResponse> recordTransaction(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {
        
        try {
            UUID authenticatedUserId = getCurrentUserId(authentication);
            
            log.info("📥 Recording transaction - Amount: {}, Sender: {}, Receiver: {}, Status: {}, Authenticated User: {}", 
                    request.getAmount(), request.getSenderId(), request.getReceiverId(), 
                    request.getStatus(), authenticatedUserId);
            
            TransactionResponse response = transactionService.createTransaction(request, authenticatedUserId);
            
            log.info("✅ Transaction recorded successfully - ID: {}, Amount: {}", 
                    response.getTransactionId(), response.getAmount());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (EntityNotFoundException e) {
            log.error("❌ User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            
        } catch (Exception e) {
            log.error("❌ Error recording transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{transactionId}")
    @Operation(summary = "Get Transaction", description = "Get transaction details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<TransactionResponse> getTransaction(
            @Parameter(description = "Transaction ID") @PathVariable UUID transactionId,
            Authentication authentication) {
        
        try {
            getCurrentUserId(authentication); // Ensure authenticated
            
            log.info("🔍 Fetching transaction: {}", transactionId);
            
            TransactionResponse response = transactionService.getTransactionById(transactionId);
            
            log.info("✅ Transaction found: {}", transactionId);
            return ResponseEntity.ok(response);
            
        } catch (EntityNotFoundException e) {
            log.error("❌ Transaction not found: {}", transactionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            
        } catch (Exception e) {
            log.error("❌ Error fetching transaction: {}", transactionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get User Transactions", description = "Get all transactions for a user (sent and received)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<TransactionResponse>> getUserTransactions(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        try {
            getCurrentUserId(authentication); // Ensure authenticated
            
            Pageable pageable = PageRequest.of(page, size);
            
            log.info("📋 Fetching transactions for user: {}, page: {}, size: {}", userId, page, size);
            
            Page<TransactionResponse> transactions = transactionService.getUserTransactions(userId, pageable);
            
            log.info("✅ Found {} transactions for user: {}", transactions.getTotalElements(), userId);
            return ResponseEntity.ok(transactions);
            
        } catch (Exception e) {
            log.error("❌ Error fetching transactions for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/sent/{senderId}")
    @Operation(summary = "Get Sent Transactions", description = "Get all transactions sent by a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<TransactionResponse>> getSentTransactions(
            @Parameter(description = "Sender ID") @PathVariable UUID senderId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        try {
            getCurrentUserId(authentication); // Ensure authenticated
            
            Pageable pageable = PageRequest.of(page, size);
            
            log.info("📤 Fetching sent transactions for user: {}, page: {}, size: {}", senderId, page, size);
            
            Page<TransactionResponse> transactions = transactionService.getSentTransactions(senderId, pageable);
            
            log.info("✅ Found {} sent transactions for user: {}", transactions.getTotalElements(), senderId);
            return ResponseEntity.ok(transactions);
            
        } catch (Exception e) {
            log.error("❌ Error fetching sent transactions for user: {}", senderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/received/{receiverId}")
    @Operation(summary = "Get Received Transactions", description = "Get all transactions received by a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<TransactionResponse>> getReceivedTransactions(
            @Parameter(description = "Receiver ID") @PathVariable UUID receiverId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        try {
            getCurrentUserId(authentication); // Ensure authenticated
            
            Pageable pageable = PageRequest.of(page, size);
            
            log.info("📥 Fetching received transactions for user: {}, page: {}, size: {}", receiverId, page, size);
            
            Page<TransactionResponse> transactions = transactionService.getReceivedTransactions(receiverId, pageable);
            
            log.info("✅ Found {} received transactions for user: {}", transactions.getTotalElements(), receiverId);
            return ResponseEntity.ok(transactions);
            
        } catch (Exception e) {
            log.error("❌ Error fetching received transactions for user: {}", receiverId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/details/user/{userId}")
    @Operation(summary = "Get Detailed Transaction Information",
               description = "Get detailed transaction information for a user including sender/receiver details, payment info, and service listing title")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction details retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<TransactionDetailsDto>> getTransactionDetails(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        try {
            getCurrentUserId(authentication); // Ensure authenticated

            Pageable pageable = PageRequest.of(page, size);

            log.info("📊 Fetching detailed transaction info for user: {}, page: {}, size: {}", userId, page, size);

            Page<TransactionDetailsDto> transactionDetails = transactionService.getTransactionDetails(userId, pageable);

            log.info("✅ Found {} detailed transactions for user: {}", transactionDetails.getTotalElements(), userId);
            return ResponseEntity.ok(transactionDetails);

        } catch (Exception e) {
            log.error("❌ Error fetching detailed transactions for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/details/all")
    @Operation(summary = "Get All Detailed Transactions",
               description = "Get all detailed transaction information with pagination (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All transaction details retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<TransactionDetailsDto>> getAllTransactionDetails(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        try {
            getCurrentUserId(authentication); // Ensure authenticated

            Pageable pageable = PageRequest.of(page, size);

            log.info("📊 Fetching all detailed transaction info, page: {}, size: {}", page, size);

            Page<TransactionDetailsDto> transactionDetails = transactionService.getAllTransactionDetails(pageable);

            log.info("✅ Found {} total detailed transactions", transactionDetails.getTotalElements());
            return ResponseEntity.ok(transactionDetails);

        } catch (Exception e) {
            log.error("❌ Error fetching all detailed transactions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Utility method
    private UUID getCurrentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserDetails)) {
            throw new IllegalStateException("User authentication required");
        }
        
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }
}
