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
        log.info("   • Transaction Amount: {}", transaction.getAmount());
        log.info("   • Sender (Member): {} ({})", 
                transaction.getSender().getFirstName() + " " + transaction.getSender().getLastName(),
                transaction.getSender().getId());
        log.info("   • Receiver (Domain Expert): {} ({})", 
                transaction.getReceiver().getFirstName() + " " + transaction.getReceiver().getLastName(),
                transaction.getReceiver().getId());
        log.info("   • Status: {}", transaction.getStatus());
        
        User domainExpert = transaction.getReceiver();
        UUID domainExpertId = domainExpert.getId();
        Integer transactionAmount = transaction.getAmount();
        
        // Calculate system charge (5%) and domain expert amount (95%)
        Integer systemChargeForThisTransaction = (int) Math.round(transactionAmount * 0.05); // 5% system charge
        Integer domainExpertAmountForThisTransaction = transactionAmount - systemChargeForThisTransaction; // 95% to domain expert
        
        log.info("────────────────────────────────────────────────────────────────");
        log.info("💰 [WALLET] CALCULATING CHARGES");
        log.info("   • Transaction Amount: {}", transactionAmount);
        log.info("   • System Charge (5%): {}", systemChargeForThisTransaction);
        log.info("   • Domain Expert Amount (95%): {}", domainExpertAmountForThisTransaction);
        
        LocalDateTime now = LocalDateTime.now();
        
        log.info("────────────────────────────────────────────────────────────────");
        log.info("🔍 [WALLET] CHECKING IF WALLET EXISTS");
        log.info("   SQL: SELECT * FROM system_wallet WHERE belongs_to = '{}'", domainExpertId);
        
        Optional<SystemWallet> existingWallet = systemWalletRepository.findByBelongsToId(domainExpertId);
        
        SystemWallet wallet;
        
        if (existingWallet.isPresent()) {
            // Wallet exists - UPDATE existing balances
            wallet = existingWallet.get();
            Integer oldHoldAmount = wallet.getHoldAmount();
            Integer oldSystemCharged = wallet.getSystemCharged() != null ? wallet.getSystemCharged() : 0; // Handle null for existing records
            Integer newHoldAmount = oldHoldAmount + domainExpertAmountForThisTransaction;
            Integer newSystemCharged = oldSystemCharged + systemChargeForThisTransaction;
            
            log.info("✅ [WALLET] WALLET FOUND - UPDATING EXISTING BALANCE");
            log.info("   📊 Current Wallet:");
            log.info("      • Wallet ID: {}", wallet.getWalletId());
            log.info("      • Current Hold Amount (95%): {}", oldHoldAmount);
            log.info("      • Current Released Amount: {}", wallet.getReleasedAmount());
            log.info("      • Current System Charged (5%): {}", oldSystemCharged);
            log.info("      • Last Transaction: {}", wallet.getLastTransactionAt());
            
            log.info("   📊 This Transaction:");
            log.info("      • Amount to Add to Hold (95%): {}", domainExpertAmountForThisTransaction);
            log.info("      • System Charge to Add (5%): {}", systemChargeForThisTransaction);
            
            log.info("   📊 New Balances:");
            log.info("      • New Hold Amount: {} + {} = {}", oldHoldAmount, domainExpertAmountForThisTransaction, newHoldAmount);
            log.info("      • New System Charged: {} + {} = {}", oldSystemCharged, systemChargeForThisTransaction, newSystemCharged);
            
            log.info("────────────────────────────────────────────────────────────────");
            log.info("💾 [WALLET] UPDATING DATABASE");
            log.info("   SQL: UPDATE system_wallet");
            log.info("        SET hold_amount = {} (was {}),", newHoldAmount, oldHoldAmount);
            log.info("            system_charged = {} (was {}),", newSystemCharged, oldSystemCharged);
            log.info("            last_transaction_at = '{}',", now);
            log.info("            updated_at = '{}'", now);
            log.info("        WHERE belongs_to = '{}';", domainExpertId);
            
            wallet.setHoldAmount(newHoldAmount);
            wallet.setSystemCharged(newSystemCharged);
            wallet.setLastTransactionAt(now);
            wallet.setUpdatedAt(now);
            wallet = systemWalletRepository.save(wallet);
            
            log.info("✅ [WALLET] BALANCE UPDATED SUCCESSFULLY!");
            log.info("   📊 Updated Wallet:");
            log.info("      • Hold Amount: {} → {} (+{})", oldHoldAmount, newHoldAmount, domainExpertAmountForThisTransaction);
            log.info("      • Released Amount: {}", wallet.getReleasedAmount());
            log.info("      • System Charged: {} → {} (+{})", oldSystemCharged, newSystemCharged, systemChargeForThisTransaction);
            log.info("      • Total Balance (Hold + Released): {}", newHoldAmount + wallet.getReleasedAmount());
            
        } else {
            // Wallet doesn't exist - CREATE new wallet
            log.info("ℹ️ [WALLET] NO WALLET FOUND - CREATING NEW WALLET");
            log.info("   📊 New Wallet Details:");
            log.info("      • Domain Expert: {} ({})", 
                    domainExpert.getFirstName() + " " + domainExpert.getLastName(),
                    domainExpertId);
            log.info("      • Initial Hold Amount (95%): {}", domainExpertAmountForThisTransaction);
            log.info("      • Initial Released Amount: 0");
            log.info("      • Initial System Charge (5%): {}", systemChargeForThisTransaction);
            log.info("      • Total Transaction: {}", transactionAmount);
            log.info("      • Status: ACTIVE");
            
            log.info("────────────────────────────────────────────────────────────────");
            log.info("💾 [WALLET] INSERTING INTO DATABASE");
            log.info("   SQL: INSERT INTO system_wallet");
            log.info("        (wallet_id, hold_amount, released_amount, system_charged, belongs_to, status, ...)");
            log.info("        VALUES (UUID, {}, 0, {}, '{}', 'ACTIVE', ...)",
                    domainExpertAmountForThisTransaction, systemChargeForThisTransaction, domainExpertId);
            
            wallet = SystemWallet.builder()
                    .holdAmount(domainExpertAmountForThisTransaction)
                    .releasedAmount(0)
                    .systemCharged(systemChargeForThisTransaction)
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
            log.info("      • Hold Amount (95%): {}", wallet.getHoldAmount());
            log.info("      • Released Amount: {}", wallet.getReleasedAmount());
            log.info("      • System Charged (5%): {}", wallet.getSystemCharged());
            log.info("      • Total Balance: {}", wallet.getHoldAmount() + wallet.getReleasedAmount());
            log.info("      • Domain Expert: {}", domainExpertId);
            log.info("      • Status: {}", wallet.getStatus());
        }
        
        log.info("════════════════════════════════════════════════════════════════");
        log.info("✅ WALLET OPERATION COMPLETED SUCCESSFULLY!");
        log.info("   Summary:");
        log.info("   • Domain Expert: {} ({})", 
                domainExpert.getFirstName() + " " + domainExpert.getLastName(),
                domainExpertId);
        Integer finalSystemCharged = wallet.getSystemCharged() != null ? wallet.getSystemCharged() : 0;
        Integer totalBalance = wallet.getHoldAmount() + wallet.getReleasedAmount();
        log.info("   • Hold Amount (within 14 days): {}", wallet.getHoldAmount());
        log.info("   • Released Amount (withdrawable): {}", wallet.getReleasedAmount());
        log.info("   • Total Balance: {}", totalBalance);
        log.info("   • System Charged (Total): {}", finalSystemCharged);
        log.info("   • Total Received: {}", totalBalance + finalSystemCharged);
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
        Integer systemCharged = wallet.getSystemCharged() != null ? wallet.getSystemCharged() : 0;
        Integer totalBalance = wallet.getHoldAmount() + wallet.getReleasedAmount();
        Integer totalReceived = totalBalance + systemCharged;
        
        log.info("✅ Wallet balance for domain expert {}: Hold={}, Released={}, TotalBalance={}, SystemCharged={}, TotalReceived={}", 
                domainExpertId, wallet.getHoldAmount(), wallet.getReleasedAmount(), totalBalance, systemCharged, totalReceived);
        
        return WalletBalanceResponse.builder()
                .domainExpertId(domainExpertId)
                .domainExpertName(domainExpert.getFirstName() + " " + domainExpert.getLastName())
                .holdAmount(wallet.getHoldAmount())
                .releasedAmount(wallet.getReleasedAmount())
                .totalBalance(totalBalance)
                .systemCharged(systemCharged)
                .totalReceived(totalReceived)
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
                .holdAmount(wallet.getHoldAmount())
                .releasedAmount(wallet.getReleasedAmount())
                .systemCharged(wallet.getSystemCharged() != null ? wallet.getSystemCharged() : 0)
                .belongsTo(domainExpert.getId())
                .domainExpertName(domainExpert.getFirstName() + " " + domainExpert.getLastName())
                .status(wallet.getStatus())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .lastTransactionAt(wallet.getLastTransactionAt())
                .build();
    }
}
