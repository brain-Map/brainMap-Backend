package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.wallet.SystemWalletResponse;
import com.app.brainmap.domain.dto.wallet.WalletBalanceResponse;
import com.app.brainmap.domain.entities.SystemWallet;
import com.app.brainmap.domain.entities.Transaction;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.repositories.SystemWalletRepository;
import com.app.brainmap.services.SystemWalletService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SystemWalletServiceImpl implements SystemWalletService {
    
    private final SystemWalletRepository systemWalletRepository;
    
    @Override
    public SystemWalletResponse addToWallet(Transaction transaction) {
        log.info("════════════════════════════════════════════════════════════════");
        log.info("💼 [WALLET] ADDING AMOUNT TO SYSTEM WALLET");
        log.info("════════════════════════════════════════════════════════════════");
        log.info("📊 Transaction Details:");
        log.info("   • Transaction ID: {}", transaction.getTransactionId());
        log.info("   • Amount to Add: {}", transaction.getAmount());
        log.info("   • Sender (Member): {} ({})", 
                transaction.getSender().getFirstName() + " " + transaction.getSender().getLastName(),
                transaction.getSender().getId());
        log.info("   • Receiver (Domain Expert): {} ({})", 
                transaction.getReceiver().getFirstName() + " " + transaction.getReceiver().getLastName(),
                transaction.getReceiver().getId());
        log.info("   • Status: {}", transaction.getStatus());
        
        User domainExpert = transaction.getReceiver();
        UUID domainExpertId = domainExpert.getId();
        Integer amountToAdd = transaction.getAmount();
        LocalDateTime now = LocalDateTime.now();
        
        log.info("────────────────────────────────────────────────────────────────");
        log.info("🔍 [WALLET] CHECKING IF WALLET EXISTS");
        log.info("   SQL: SELECT * FROM system_wallet WHERE belongs_to = '{}'", domainExpertId);
        
        Optional<SystemWallet> existingWallet = systemWalletRepository.findByBelongsToId(domainExpertId);
        
        SystemWallet wallet;
        
        if (existingWallet.isPresent()) {
            // Wallet exists - UPDATE existing balance
            wallet = existingWallet.get();
            Integer oldAmount = wallet.getAmount();
            Integer newAmount = oldAmount + amountToAdd;
            
            log.info("✅ [WALLET] WALLET FOUND - UPDATING EXISTING BALANCE");
            log.info("   📊 Current Wallet:");
            log.info("      • Wallet ID: {}", wallet.getWalletId());
            log.info("      • Current Balance: {}", oldAmount);
            log.info("      • Amount to Add: {}", amountToAdd);
            log.info("      • New Balance: {}", newAmount);
            log.info("      • Last Transaction: {}", wallet.getLastTransactionAt());
            
            log.info("────────────────────────────────────────────────────────────────");
            log.info("💾 [WALLET] UPDATING DATABASE");
            log.info("   SQL: UPDATE system_wallet");
            log.info("        SET amount = {} (was {}),", newAmount, oldAmount);
            log.info("            last_transaction_at = '{}',", now);
            log.info("            updated_at = '{}'", now);
            log.info("        WHERE belongs_to = '{}';", domainExpertId);
            
            wallet.setAmount(newAmount);
            wallet.setLastTransactionAt(now);
            wallet.setUpdatedAt(now);
            wallet = systemWalletRepository.save(wallet);
            
            log.info("✅ [WALLET] BALANCE UPDATED SUCCESSFULLY!");
            log.info("   📊 Updated Wallet:");
            log.info("      • Wallet ID: {}", wallet.getWalletId());
            log.info("      • Balance: {} → {}", oldAmount, newAmount);
            log.info("      • Increment: +{}", amountToAdd);
            
        } else {
            // Wallet doesn't exist - CREATE new wallet
            log.info("ℹ️ [WALLET] NO WALLET FOUND - CREATING NEW WALLET");
            log.info("   📊 New Wallet Details:");
            log.info("      • Domain Expert: {} ({})", 
                    domainExpert.getFirstName() + " " + domainExpert.getLastName(),
                    domainExpertId);
            log.info("      • Initial Balance: {}", amountToAdd);
            log.info("      • Status: ACTIVE");
            
            log.info("────────────────────────────────────────────────────────────────");
            log.info("💾 [WALLET] INSERTING INTO DATABASE");
            log.info("   SQL: INSERT INTO system_wallet");
            log.info("        (wallet_id, amount, belongs_to, status, created_at, updated_at, last_transaction_at)");
            log.info("        VALUES (UUID, {}, '{}', 'ACTIVE', NOW(), NOW(), NOW())",
                    amountToAdd, domainExpertId);
            
            wallet = SystemWallet.builder()
                    .amount(amountToAdd)
                    .belongsTo(domainExpert)
                    .status("ACTIVE")
                    .createdAt(now)
                    .updatedAt(now)
                    .lastTransactionAt(now)
                    .build();
            
            wallet = systemWalletRepository.save(wallet);
            
            log.info("✅ [WALLET] NEW WALLET CREATED SUCCESSFULLY!");
            log.info("   📊 Wallet Details:");
            log.info("      • Wallet ID: {}", wallet.getWalletId());
            log.info("      • Initial Balance: {}", wallet.getAmount());
            log.info("      • Domain Expert: {}", domainExpertId);
            log.info("      • Status: {}", wallet.getStatus());
        }
        
        log.info("════════════════════════════════════════════════════════════════");
        log.info("✅ WALLET OPERATION COMPLETED SUCCESSFULLY!");
        log.info("   Summary:");
        log.info("   • Domain Expert: {} ({})", 
                domainExpert.getFirstName() + " " + domainExpert.getLastName(),
                domainExpertId);
        log.info("   • Current Balance: {}", wallet.getAmount());
        log.info("   • Last Updated: {}", wallet.getUpdatedAt());
        log.info("════════════════════════════════════════════════════════════════");
        
        return mapToResponse(wallet);
    }
    
    @Override
    public WalletBalanceResponse getWalletBalance(UUID domainExpertId) {
        log.info("💰 Fetching wallet balance for domain expert: {}", domainExpertId);
        
        SystemWallet wallet = systemWalletRepository.findByBelongsToId(domainExpertId)
                .orElseThrow(() -> {
                    log.error("❌ Wallet not found for domain expert: {}", domainExpertId);
                    return new EntityNotFoundException("Wallet not found for domain expert: " + domainExpertId);
                });
        
        User domainExpert = wallet.getBelongsTo();
        
        log.info("✅ Wallet balance for domain expert {}: {}", domainExpertId, wallet.getAmount());
        
        return WalletBalanceResponse.builder()
                .domainExpertId(domainExpertId)
                .domainExpertName(domainExpert.getFirstName() + " " + domainExpert.getLastName())
                .currentBalance(wallet.getAmount())
                .status(wallet.getStatus())
                .lastTransactionAt(wallet.getLastTransactionAt())
                .createdAt(wallet.getCreatedAt())
                .build();
    }
    
    @Override
    public SystemWalletResponse getWallet(UUID domainExpertId) {
        log.info("� Fetching wallet for domain expert: {}", domainExpertId);
        
        SystemWallet wallet = systemWalletRepository.findByBelongsToId(domainExpertId)
                .orElseThrow(() -> {
                    log.error("❌ Wallet not found for domain expert: {}", domainExpertId);
                    return new EntityNotFoundException("Wallet not found for domain expert: " + domainExpertId);
                });
        
        log.info("✅ Wallet found for domain expert: {}", domainExpertId);
        return mapToResponse(wallet);
    }
    
    @Override
    public Page<SystemWalletResponse> getAllWallets(Pageable pageable) {
        log.info("� Fetching all wallets, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<SystemWallet> wallets = systemWalletRepository.findAllByOrderByUpdatedAtDesc(pageable);
        
        log.info("✅ Found {} total wallets", wallets.getTotalElements());
        return wallets.map(this::mapToResponse);
    }
    
    /**
     * Map SystemWallet entity to SystemWalletResponse DTO
     */
    private SystemWalletResponse mapToResponse(SystemWallet wallet) {
        User domainExpert = wallet.getBelongsTo();
        
        return SystemWalletResponse.builder()
                .walletId(wallet.getWalletId())
                .amount(wallet.getAmount())
                .belongsTo(domainExpert.getId())
                .domainExpertName(domainExpert.getFirstName() + " " + domainExpert.getLastName())
                .status(wallet.getStatus())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .lastTransactionAt(wallet.getLastTransactionAt())
                .build();
    }
}
