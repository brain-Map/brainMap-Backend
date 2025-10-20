package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.wallet.SystemWalletResponse;
import com.app.brainmap.domain.dto.wallet.WalletBalanceResponse;
import com.app.brainmap.domain.dto.wallet.SystemWalletTotalsResponse;
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
        User domainExpert = transaction.getReceiver();
        UUID domainExpertId = domainExpert.getId();
        Integer transactionAmount = transaction.getAmount();
        
        // Calculate system charge (5%) and domain expert amount (95%)
        Integer systemChargeForThisTransaction = (int) Math.round(transactionAmount * 0.05); // 5% system charge
        Integer domainExpertAmountForThisTransaction = transactionAmount - systemChargeForThisTransaction; // 95% to domain expert

        LocalDateTime now = LocalDateTime.now();
        
        Optional<SystemWallet> existingWallet = systemWalletRepository.findByBelongsToId(domainExpertId);
        
        SystemWallet wallet;
        
        if (existingWallet.isPresent()) {
            // Wallet exists - UPDATE existing balances
            wallet = existingWallet.get();
            Integer oldHoldAmount = wallet.getHoldAmount();
            Integer oldSystemCharged = wallet.getSystemCharged() != null ? wallet.getSystemCharged() : 0; // Handle null for existing records
            Integer newHoldAmount = oldHoldAmount + domainExpertAmountForThisTransaction;
            Integer newSystemCharged = oldSystemCharged + systemChargeForThisTransaction;

            wallet.setHoldAmount(newHoldAmount);
            wallet.setSystemCharged(newSystemCharged);
            wallet.setLastTransactionAt(now);
            wallet.setUpdatedAt(now);
            wallet = systemWalletRepository.save(wallet);
            
        } else {
            // Wallet doesn't exist - CREATE new wallet
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
            
        }
        
        return mapToResponse(wallet);
    }
    
    @Override
    public WalletBalanceResponse getWalletBalance(UUID domainExpertId) {

        SystemWallet wallet = systemWalletRepository.findByBelongsToId(domainExpertId)
                .orElseThrow(() -> {
                    return new EntityNotFoundException("Wallet not found for domain expert: " + domainExpertId);
                });
        
        User domainExpert = wallet.getBelongsTo();
        Integer systemCharged = wallet.getSystemCharged() != null ? wallet.getSystemCharged() : 0;
        Integer totalBalance = wallet.getHoldAmount() + wallet.getReleasedAmount();
        Integer totalReceived = totalBalance + systemCharged;
        
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
        Page<SystemWallet> wallets = systemWalletRepository.findAllByOrderByUpdatedAtDesc(pageable);
        
        log.info("Found {} total wallets", wallets.getTotalElements());
        return wallets.map(this::mapToResponse);
    }

    @Override
    public SystemWalletTotalsResponse getTotals() {
        SystemWalletTotalsResponse totals = systemWalletRepository.getTotals();
        log.info("Computed wallet totals");
        return totals;
    }
    
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
