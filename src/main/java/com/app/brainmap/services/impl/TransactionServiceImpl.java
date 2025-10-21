package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.transaction.TransactionDetailsDto;
import com.app.brainmap.domain.dto.transaction.TransactionRequest;
import com.app.brainmap.domain.dto.transaction.TransactionResponse;
import com.app.brainmap.domain.entities.PaymentSession;
import com.app.brainmap.domain.entities.Transaction;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.TransactionMapper;
import com.app.brainmap.repositories.PaymentSessionRepository;
import com.app.brainmap.repositories.TransactionRepository;
import com.app.brainmap.services.SystemWalletService;
import com.app.brainmap.services.TransactionService;
import com.app.brainmap.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final PaymentSessionRepository paymentSessionRepository;
    private final UserService userService;
    private final TransactionMapper transactionMapper;
    private final SystemWalletService systemWalletService;

    @Override
    public TransactionResponse createTransaction(TransactionRequest request, UUID authenticatedUserId) {
        log.info("💰 Creating transaction - Amount: {}, Sender: {}, Receiver: {}, Status: {}, PaymentId: {}",
                request.getAmount(), request.getSenderId(), request.getReceiverId(), request.getStatus(), request.getPaymentId());

        // Validate that sender and receiver are different
        if (request.getSenderId().equals(request.getReceiverId())) {
            log.error("❌ Sender and receiver cannot be the same user: {}", request.getSenderId());
            throw new IllegalArgumentException("Sender and receiver cannot be the same user");
        }
        
        // Fetch PaymentSession by paymentId
        PaymentSession paymentSession = null;
        if (request.getPaymentId() != null && !request.getPaymentId().isBlank()) {
            paymentSession = paymentSessionRepository.findByPaymentId(request.getPaymentId())
                    .orElseThrow(() -> {
                        log.error("❌ PaymentSession not found: {}", request.getPaymentId());
                        return new EntityNotFoundException("PaymentSession not found with ID: " + request.getPaymentId());
                    });
            log.info("✅ PaymentSession found: {} with amount: {}", paymentSession.getPaymentId(), paymentSession.getAmount());
        }

        // Validate sender exists
        User sender;
        try {
            sender = userService.getUserById(request.getSenderId());
            log.info("✅ Sender validated: {} ({})", sender.getFirstName() + " " + sender.getLastName(), sender.getId());
        } catch (Exception e) {
            log.error("❌ Sender not found: {}", request.getSenderId());
            throw new EntityNotFoundException("Sender not found with ID: " + request.getSenderId());
        }
        
        // Validate receiver exists
        User receiver;
        try {
            receiver = userService.getUserById(request.getReceiverId());
            log.info("✅ Receiver validated: {} ({})", receiver.getFirstName() + " " + receiver.getLastName(), receiver.getId());
        } catch (Exception e) {
            log.error("❌ Receiver not found: {}", request.getReceiverId());
            throw new EntityNotFoundException("Receiver not found with ID: " + request.getReceiverId());
        }
        
        // Optional: Verify authenticated user is the sender
        if (!authenticatedUserId.equals(request.getSenderId())) {
            log.warn("⚠️ Authenticated user {} is not the sender {}", authenticatedUserId, request.getSenderId());
            // You can uncomment this to enforce sender must be authenticated user
            // throw new IllegalArgumentException("You can only create transactions as the sender");
        }

        // Create transaction with PaymentSession
        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .sender(sender)
                .receiver(receiver)
                .status(request.getStatus())
                .paymentType(com.app.brainmap.domain.PaymentType.PAYMENT)
                .createdAt(LocalDateTime.now())
                .paymentSession(paymentSession)  // Set the PaymentSession
                .build();
        
        // Save transaction
        transaction = transactionRepository.save(transaction);
        
        log.info("✅ Transaction created successfully - ID: {}, Amount: {}, From: {} To: {}, PaymentSession: {}",
                transaction.getTransactionId(), transaction.getAmount(),
                sender.getFirstName() + " " + sender.getLastName(),
                receiver.getFirstName() + " " + receiver.getLastName(),
                paymentSession != null ? paymentSession.getPaymentId() : "N/A");

        // Automatically add amount to domain expert's system wallet
        log.info("🔗 Triggering system wallet update for domain expert: {}", receiver.getId());
        try {
            systemWalletService.addToWallet(transaction);
            log.info("✅ System wallet updated successfully for domain expert: {}", receiver.getId());
        } catch (Exception e) {
            log.error("❌ Failed to update system wallet for transaction: {}", transaction.getTransactionId(), e);
            // Note: We don't throw the exception here to avoid rolling back the transaction
            // The wallet can be updated later through a retry mechanism or admin action
        }

        return mapToResponse(transaction);
    }
    
    @Override
    public TransactionResponse getTransactionById(UUID transactionId) {
        log.info("🔍 Fetching transaction: {}", transactionId);
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.error("❌ Transaction not found: {}", transactionId);
                    return new EntityNotFoundException("Transaction not found with ID: " + transactionId);
                });
        
        log.info("✅ Transaction found: {}", transactionId);
        return mapToResponse(transaction);
    }
    
    @Override
    public Page<TransactionResponse> getUserTransactions(UUID userId, Pageable pageable) {
        log.info("📋 Fetching all transactions for user: {}", userId);
        
        Page<Transaction> transactions = transactionRepository.findByUserId(userId, pageable);
        
        log.info("✅ Found {} transactions for user: {}", transactions.getTotalElements(), userId);
        return transactions.map(this::mapToResponse);
    }
    
    @Override
    public Page<TransactionResponse> getSentTransactions(UUID senderId, Pageable pageable) {
        log.info("📤 Fetching sent transactions for user: {}", senderId);
        
        Page<Transaction> transactions = transactionRepository.findBySenderIdOrderByCreatedAtDesc(senderId, pageable);
        
        log.info("✅ Found {} sent transactions for user: {}", transactions.getTotalElements(), senderId);
        return transactions.map(this::mapToResponse);
    }
    
    @Override
    public Page<TransactionResponse> getReceivedTransactions(UUID receiverId, Pageable pageable) {
        log.info("📥 Fetching received transactions for user: {}", receiverId);
        
        Page<Transaction> transactions = transactionRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId, pageable);
        
        log.info("✅ Found {} received transactions for user: {}", transactions.getTotalElements(), receiverId);
        return transactions.map(this::mapToResponse);
    }
    
    @Override
    public Page<TransactionDetailsDto> getTransactionDetails(UUID userId, Pageable pageable) {
        log.info("📊 Fetching detailed transactions for user: {}", userId);

        Page<Transaction> transactions = transactionRepository.findTransactionDetailsForUser(userId, pageable);

        log.info("✅ Found {} detailed transactions for user: {}", transactions.getTotalElements(), userId);
        return transactions.map(transactionMapper::toTransactionDetailsDto);
    }

    @Override
    public Page<TransactionDetailsDto> getAllTransactionDetails(Pageable pageable) {
        log.info("📊 Fetching all detailed transactions");

        Page<Transaction> transactions = transactionRepository.findAllTransactionDetails(pageable);

        log.info("✅ Found {} total detailed transactions", transactions.getTotalElements());
        return transactions.map(transactionMapper::toTransactionDetailsDto);
    }

    @Override
    public List<TransactionDetailsDto> getAllTransactionDetails() {
        log.info("📊 Fetching all detailed transactions (non-paginated)");
        List<Transaction> transactions = transactionRepository.findAllTransactionDetailsList();
        log.info("✅ Found {} total detailed transactions (non-paginated)", transactions.size());
        return transactions.stream()
                .map(transactionMapper::toTransactionDetailsDto)
                .collect(Collectors.toList());
    }

    /**
     * Map Transaction entity to TransactionResponse DTO
     */
    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .amount(transaction.getAmount())
                .senderId(transaction.getSender() != null ?  transaction.getSender().getId() : null )
                .senderName(transaction.getSender() != null ? transaction.getSender().getFirstName() + " " + transaction.getSender().getLastName() : null)
                .receiverId(transaction.getReceiver() != null ? transaction.getReceiver().getId() : null)
                .receiverName(transaction.getReceiver() != null ? transaction.getReceiver().getFirstName() + " " + transaction.getReceiver().getLastName() : null)
                .status(transaction.getStatus())
                .paymentType(transaction.getPaymentType() != null ? transaction.getPaymentType().name() : null)
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
