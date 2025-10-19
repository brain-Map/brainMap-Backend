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
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SystemWalletServiceImpl implements SystemWalletService {
    
    private final SystemWalletRepository systemWalletRepository;
    
    @Override
    public SystemWalletResponse createWalletEntry(Transaction transaction) {
        log.info("════════════════════════════════════════════════════════════════");
        log.info("💼 [WALLET] CREATING SYSTEM WALLET ENTRY");
        log.info("════════════════════════════════════════════════════════════════");
        log.info("📊 Transaction Details:");
        log.info("   • Transaction ID: {}", transaction.getTransactionId());
        log.info("   • Amount: {}", transaction.getAmount());
        log.info("   • Sender (Member): {} ({})", 
                transaction.getSender().getFirstName() + " " + transaction.getSender().getLastName(),
                transaction.getSender().getId());
        log.info("   • Receiver (Domain Expert): {} ({})", 
                transaction.getReceiver().getFirstName() + " " + transaction.getReceiver().getLastName(),
                transaction.getReceiver().getId());
        log.info("   • Status: {}", transaction.getStatus());
        
        // Create wallet entry - amount goes to system wallet for the domain expert (receiver)
        SystemWallet walletEntry = SystemWallet.builder()
                .amount(transaction.getAmount())
                .belongsTo(transaction.getReceiver()) // Domain expert who will receive this
                .transaction(transaction)
                .status("PENDING") // Initial status is PENDING
                .createdAt(LocalDateTime.now())
                .build();
        
        log.info("────────────────────────────────────────────────────────────────");
        log.info("💾 [WALLET] SAVING TO DATABASE");
        log.info("   SQL: INSERT INTO system_wallet");
        log.info("        (wallet_id, amount, belongs_to, transaction_id, status, created_at)");
        log.info("        VALUES (UUID, {}, '{}', '{}', 'PENDING', NOW())",
                walletEntry.getAmount(),
                walletEntry.getBelongsTo().getId(),
                walletEntry.getTransaction().getTransactionId());
        
        walletEntry = systemWalletRepository.save(walletEntry);
        
        log.info("✅ [WALLET] SYSTEM WALLET ENTRY CREATED SUCCESSFULLY!");
        log.info("   📊 Wallet Entry Details:");
        log.info("      • Wallet ID: {}", walletEntry.getWalletId());
        log.info("      • Amount: {}", walletEntry.getAmount());
        log.info("      • Belongs To (Domain Expert): {}", walletEntry.getBelongsTo().getId());
        log.info("      • Transaction ID: {}", walletEntry.getTransaction().getTransactionId());
        log.info("      • Status: {}", walletEntry.getStatus());
        log.info("      • Created At: {}", walletEntry.getCreatedAt());
        log.info("════════════════════════════════════════════════════════════════");
        
        return mapToResponse(walletEntry);
    }
    
    @Override
    public WalletBalanceResponse getWalletBalance(UUID domainExpertId) {
        log.info("💰 Fetching wallet balance for domain expert: {}", domainExpertId);
        
        Integer totalBalance = systemWalletRepository.getTotalBalance(domainExpertId);
        
        log.info("✅ Total balance for domain expert {}: {}", domainExpertId, totalBalance);
        
        // You can add more calculations here (withdrawn amount, transaction count, etc.)
        return WalletBalanceResponse.builder()
                .domainExpertId(domainExpertId)
                .totalBalance(totalBalance)
                .pendingAmount(totalBalance)
                .build();
    }
    
    @Override
    public Page<SystemWalletResponse> getWalletEntries(UUID domainExpertId, Pageable pageable) {
        log.info("📋 Fetching wallet entries for domain expert: {}", domainExpertId);
        
        Page<SystemWallet> walletEntries = systemWalletRepository
                .findByBelongsToIdOrderByCreatedAtDesc(domainExpertId, pageable);
        
        log.info("✅ Found {} wallet entries for domain expert: {}", 
                walletEntries.getTotalElements(), domainExpertId);
        
        return walletEntries.map(this::mapToResponse);
    }
    
    @Override
    public SystemWalletResponse getWalletEntryByTransaction(UUID transactionId) {
        log.info("🔍 Fetching wallet entry for transaction: {}", transactionId);
        
        SystemWallet walletEntry = systemWalletRepository.findByTransactionTransactionId(transactionId)
                .orElseThrow(() -> {
                    log.error("❌ Wallet entry not found for transaction: {}", transactionId);
                    return new EntityNotFoundException("Wallet entry not found for transaction: " + transactionId);
                });
        
        log.info("✅ Wallet entry found for transaction: {}", transactionId);
        return mapToResponse(walletEntry);
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
                .transactionId(wallet.getTransaction().getTransactionId())
                .status(wallet.getStatus())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .withdrawnAt(wallet.getWithdrawnAt())
                .build();
    }
}
