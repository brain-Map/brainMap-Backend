package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.wallet.SystemWalletResponse;
import com.app.brainmap.domain.dto.wallet.WalletBalanceResponse;
import com.app.brainmap.domain.dto.wallet.SystemWalletTotalsResponse;
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
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "System Wallet Management", description = "APIs for managing domain expert wallet balances")
public class SystemWalletController {
    
    private final SystemWalletService systemWalletService;
    
    @GetMapping("/balance/{domainExpertId}")
    @Operation(summary = "Get Wallet Balance", description = "Get wallet balance for a domain expert")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT authentication required"),
        @ApiResponse(responseCode = "404", description = "Wallet not found for domain expert"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<WalletBalanceResponse> getWalletBalance(
            @Parameter(description = "Domain Expert ID") @PathVariable UUID domainExpertId,
            Authentication authentication) {
        
        try {
            getCurrentUserId(authentication); // Ensure authenticated
            log.info("💰 Fetching wallet balance for domain expert: {}", domainExpertId);
            WalletBalanceResponse balance = systemWalletService.getWalletBalance(domainExpertId);
            return ResponseEntity.ok(balance);
            
        } catch (EntityNotFoundException e) {
            log.error("❌ Wallet not found for domain expert: {}", domainExpertId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            
        } catch (Exception e) {
            log.error("❌ Error fetching wallet balance for: {}", domainExpertId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{domainExpertId}")
    @Operation(summary = "Get Wallet", description = "Get wallet details for a domain expert")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Wallet retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Wallet not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })


    public ResponseEntity<SystemWalletResponse> getWallet(
            @Parameter(description = "Domain Expert ID") @PathVariable UUID domainExpertId,
            Authentication authentication) {
        
        try {
            getCurrentUserId(authentication); // Ensure authenticated
            log.info("� Fetching wallet for domain expert: {}", domainExpertId);
            
            SystemWalletResponse wallet = systemWalletService.getWallet(domainExpertId);
            return ResponseEntity.ok(wallet);
            
        } catch (EntityNotFoundException e) {
            log.error("❌ Wallet not found for domain expert: {}", domainExpertId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            
        } catch (Exception e) {
            log.error("❌ Error fetching wallet for: {}", domainExpertId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/all")
    @Operation(summary = "Get All Wallets", description = "Get all wallets with pagination (Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Wallets retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<SystemWalletResponse>> getAllWallets(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        try {
            getCurrentUserId(authentication); // Ensure authenticated
            
            Pageable pageable = PageRequest.of(page, size);
            log.info("Fetching all wallets, page: {}, size: {}", page, size);
            
            Page<SystemWalletResponse> wallets = systemWalletService.getAllWallets(pageable);
            log.info("Found {} total wallets", wallets.getTotalElements());
            return ResponseEntity.ok(wallets);
            
        } catch (Exception e) {
            log.error("❌ Error fetching all wallets", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/totals")
    @Operation(summary = "Get Wallet Totals", description = "Get sum totals for hold, released, and system charged amounts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Totals retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SystemWalletTotalsResponse> getWalletTotals(Authentication authentication) {
        try {
            getCurrentUserId(authentication); // Ensure authenticated
            log.info("Fetching wallet totals");
            SystemWalletTotalsResponse totals = systemWalletService.getTotals();
            return ResponseEntity.ok(totals);
        } catch (Exception e) {
            log.error("❌ Error fetching wallet totals", e);
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
