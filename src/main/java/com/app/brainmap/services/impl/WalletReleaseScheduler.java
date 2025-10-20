package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.SystemWallet;
import com.app.brainmap.domain.entities.Transaction;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.repositories.SystemWalletRepository;
import com.app.brainmap.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled service that automatically moves amounts from hold_amount to released_amount
 * after 14 days have passed since the transaction was created.
 * 
 * Runs daily at 2:00 AM to check for transactions that need to be released.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WalletReleaseScheduler {

    private final TransactionRepository transactionRepository;
    private final SystemWalletRepository systemWalletRepository;

    // FOR TESTING: 30 minutes hold period
    // FOR PRODUCTION: Change back to 14 days
    private static final int HOLD_PERIOD_DAYS = 14;
    private static final int HOLD_PERIOD_MINUTES = 30; // FOR TESTING ONLY

    /**
     * Scheduled task that runs every 5 minutes (FOR TESTING)
     * Checks for transactions older than 30 minutes (FOR TESTING) and releases the held amounts
     * 
     * NOTE: For production:
     * - Change cron to "0 0 2 * * *" (daily at 2:00 AM)
     * - Use HOLD_PERIOD_DAYS instead of HOLD_PERIOD_MINUTES
     */
    @Scheduled(cron = "0 */5 * * * *") // Every 5 minutes (FOR TESTING)
    @Transactional
    public void releaseHeldAmounts() {
        log.info("════════════════════════════════════════════════════════════════");
        log.info("🕐 [SCHEDULED RELEASE] Starting automatic wallet release job");
        log.info("   Time: {}", LocalDateTime.now());
        log.info("   Hold Period: {} minutes (TESTING MODE)", HOLD_PERIOD_MINUTES);
        log.info("════════════════════════════════════════════════════════════════");

        // Calculate cutoff date (30 minutes ago FOR TESTING)
        // FOR PRODUCTION: Use minusDays(HOLD_PERIOD_DAYS)
        LocalDateTime cutoffDate = LocalDateTime.now().minusMinutes(HOLD_PERIOD_MINUTES);
        log.info("📅 Cutoff Date: {}", cutoffDate);
        log.info("   Transactions created before this date will be released");

        // Find all unreleased transactions older than 14 days
        List<Transaction> transactionsToRelease = transactionRepository
                .findUnreleasedTransactionsOlderThan(cutoffDate);

        if (transactionsToRelease.isEmpty()) {
            log.info("✅ No transactions to release at this time");
            log.info("════════════════════════════════════════════════════════════════");
            return;
        }

        log.info("📦 Found {} transactions ready for release", transactionsToRelease.size());
        log.info("────────────────────────────────────────────────────────────────");

        int successCount = 0;
        int failureCount = 0;

        for (Transaction transaction : transactionsToRelease) {
            try {
                releaseTransaction(transaction);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                log.error("❌ Failed to release transaction {}: {}", 
                        transaction.getTransactionId(), e.getMessage(), e);
            }
        }

        log.info("════════════════════════════════════════════════════════════════");
        log.info("✅ [SCHEDULED RELEASE] Job completed");
        log.info("   Total Processed: {}", transactionsToRelease.size());
        log.info("   Successful: {}", successCount);
        log.info("   Failed: {}", failureCount);
        log.info("════════════════════════════════════════════════════════════════");
    }

    /**
     * Release a single transaction by moving amount from hold to released
     */
    private void releaseTransaction(Transaction transaction) {
        log.info("💰 Processing Transaction {}", transaction.getTransactionId());
        log.info("   Amount: {}", transaction.getAmount());
        log.info("   Created: {}", transaction.getCreatedAt());
        log.info("   Receiver: {}", transaction.getReceiver().getId());

        // Calculate the amount to release (95% of transaction amount)
        Integer transactionAmount = transaction.getAmount();
        Integer amountToRelease = (int) Math.round(transactionAmount * 0.95);

        log.info("   Amount to Release (95%): {}", amountToRelease);

        // Get the receiver's wallet
        User receiver = transaction.getReceiver();
        SystemWallet wallet = systemWalletRepository.findByBelongsToId(receiver.getId())
                .orElseThrow(() -> {
                    log.error("❌ Wallet not found for receiver: {}", receiver.getId());
                    return new RuntimeException("Wallet not found for receiver: " + receiver.getId());
                });

        log.info("   📊 Current Wallet State:");
        log.info("      • Wallet ID: {}", wallet.getWalletId());
        log.info("      • Hold Amount: {}", wallet.getHoldAmount());
        log.info("      • Released Amount: {}", wallet.getReleasedAmount());
        log.info("      • System Charged: {}", wallet.getSystemCharged());

        // Move amount from hold to released
        Integer currentHoldAmount = wallet.getHoldAmount();
        Integer currentReleasedAmount = wallet.getReleasedAmount();

        if (currentHoldAmount < amountToRelease) {
            log.warn("⚠️ Hold amount ({}) is less than amount to release ({})! Releasing available amount only.",
                    currentHoldAmount, amountToRelease);
            amountToRelease = currentHoldAmount;
        }

        Integer newHoldAmount = currentHoldAmount - amountToRelease;
        Integer newReleasedAmount = currentReleasedAmount + amountToRelease;

        wallet.setHoldAmount(newHoldAmount);
        wallet.setReleasedAmount(newReleasedAmount);
        wallet.setUpdatedAt(LocalDateTime.now());

        systemWalletRepository.save(wallet);

        log.info("   📊 New Wallet State:");
        log.info("      • Hold Amount: {} → {}", currentHoldAmount, newHoldAmount);
        log.info("      • Released Amount: {} → {}", currentReleasedAmount, newReleasedAmount);
        log.info("      • Total Balance: {}", newHoldAmount + newReleasedAmount);

        // Mark transaction as released
        transaction.setAmountReleased(true);
        transactionRepository.save(transaction);

        log.info("✅ Transaction {} released successfully", transaction.getTransactionId());
        log.info("────────────────────────────────────────────────────────────────");
    }
}
