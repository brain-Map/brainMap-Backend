package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.wallet.SystemWalletResponse;
import com.app.brainmap.domain.dto.wallet.WalletBalanceResponse;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.SystemWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
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
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "System Wallet Management", description = "APIs for managing domain expert wallet balances")
public class SystemWalletController {
    
    private final SystemWalletService systemWalletService;
    
    @GetMapping("/balance/{domainExpertId}")
    @Operation(summary = "Get Wallet Balance", description = "Get total wallet balance for a domain expert")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT authentication required"),
        @ApiResponse(responseCode = "404", description = "Domain expert not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<WalletBalanceResponse> getWalletBalance(
            @Parameter(description = "Domain Expert ID") @PathVariable UUID domainExpertId,
            Authentication authentication) {
        
        try {
            getCurrentUserId(authentication); // Ensure authenticated
            
            log.info("💰 Fetching wallet balance for domain expert: {}", domainExpertId);
            
            WalletBalanceResponse balance = systemWalletService.getWalletBalance(domainExpertId);
            
            log.info("✅ Balance retrieved: {}", balance.getTotalBalance());
            return ResponseEntity.ok(balance);
            
        } catch (EntityNotFoundException e) {
            log.error("❌ Domain expert not found: {}", domainExpertId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            
        } catch (Exception e) {
            log.error("❌ Error fetching wallet balance for: {}", domainExpertId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/entries/{domainExpertId}")
    @Operation(summary = "Get Wallet Entries", description = "Get all wallet entries for a domain expert")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Wallet entries retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<SystemWalletResponse>> getWalletEntries(
            @Parameter(description = "Domain Expert ID") @PathVariable UUID domainExpertId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        try {
            getCurrentUserId(authentication); // Ensure authenticated
            
            Pageable pageable = PageRequest.of(page, size);
            
            log.info("📋 Fetching wallet entries for domain expert: {}, page: {}, size: {}", 
                    domainExpertId, page, size);
            
            Page<SystemWalletResponse> entries = systemWalletService.getWalletEntries(domainExpertId, pageable);
            
            log.info("✅ Found {} wallet entries for domain expert: {}", 
                    entries.getTotalElements(), domainExpertId);
            return ResponseEntity.ok(entries);
            
        } catch (Exception e) {
            log.error("❌ Error fetching wallet entries for: {}", domainExpertId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get Wallet Entry by Transaction", 
               description = "Get wallet entry associated with a specific transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Wallet entry found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Wallet entry not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SystemWalletResponse> getWalletEntryByTransaction(
            @Parameter(description = "Transaction ID") @PathVariable UUID transactionId,
            Authentication authentication) {
        
        try {
            getCurrentUserId(authentication); // Ensure authenticated
            
            log.info("🔍 Fetching wallet entry for transaction: {}", transactionId);
            
            SystemWalletResponse entry = systemWalletService.getWalletEntryByTransaction(transactionId);
            
            log.info("✅ Wallet entry found for transaction: {}", transactionId);
            return ResponseEntity.ok(entry);
            
        } catch (EntityNotFoundException e) {
            log.error("❌ Wallet entry not found for transaction: {}", transactionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            
        } catch (Exception e) {
            log.error("❌ Error fetching wallet entry for transaction: {}", transactionId, e);
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
