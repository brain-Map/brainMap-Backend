package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.TransactionDto;
import com.app.brainmap.domain.dto.transaction.TransactionDetailsDto;
import com.app.brainmap.domain.PaymentStatus;
import com.app.brainmap.domain.PaymentType;
import com.app.brainmap.domain.entities.PaymentSession;
import com.app.brainmap.domain.entities.Transaction;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.domain.entities.DomainExpert.ServiceBooking;
import com.app.brainmap.domain.entities.DomainExpert.ServiceListing;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    default TransactionDto toDto(Transaction tx) {
        if (tx == null) return null;

        TransactionDto.TransactionDtoBuilder builder = TransactionDto.builder()
                .transactionId(tx.getTransactionId())
                .amount(tx.getAmount())
                .status(tx.getStatus())
                .createdAt(tx.getCreatedAt());

        if (tx.getSender() != null) {
            builder.senderId(tx.getSender().getId());
        }
        if (tx.getReceiver() != null) {
            builder.receiverId(tx.getReceiver().getId());
        }

        PaymentSession ps = tx.getPaymentSession();
        if (ps != null) {
            builder.paymentSessionId(ps.getId());
        }

        return builder.build();
    }

    default TransactionDetailsDto toTransactionDetailsDto(Transaction transaction) {
        if (transaction == null) return null;

        TransactionDetailsDto.TransactionDetailsDtoBuilder builder = TransactionDetailsDto.builder()
                .transactionId(transaction.getTransactionId())
                .createdAt(transaction.getCreatedAt());

        // Map sender information
        User sender = transaction.getSender();
        if (sender != null) {
            String senderName = buildFullName(sender.getFirstName(), sender.getLastName(), sender.getUsername());
            builder.senderName(senderName)
                   .senderEmail(sender.getEmail())
                   .senderRole(sender.getUserRole());
        }

        // Map receiver information
        User receiver = transaction.getReceiver();
        if (receiver != null) {
            String receiverName = buildFullName(receiver.getFirstName(), receiver.getLastName(), receiver.getUsername());
            builder.receiverName(receiverName)
                   .receiverEmail(receiver.getEmail())
                   .receiverRole(receiver.getUserRole());
        }

        // Map payment session information
        PaymentSession paymentSession = transaction.getPaymentSession();
        if (paymentSession != null) {
            builder.amount(paymentSession.getAmount())
                   .status(paymentSession.getStatus())
                   // When payment session exists (payment_id not null), treat as PAYMENT
                   .paymentType(PaymentType.PAYMENT);

            // Map service listing information from booking
            ServiceBooking booking = paymentSession.getServiceBooking();
            if (booking != null) {
                ServiceListing service = booking.getService();
                if (service != null) {
                    builder.serviceListTitle(service.getTitle());
                }
            }
        } else {
            // Fallback to transaction amount if payment session is null
            if (transaction.getAmount() != null) {
                builder.amount(new BigDecimal(transaction.getAmount()));
            }
            // Parse status from transaction status string
            builder.status(parsePaymentStatus(transaction.getStatus()));
            // payment_id is null -> take payment type from transaction table
            builder.paymentType(transaction.getPaymentType() != null ? transaction.getPaymentType() : PaymentType.PAYMENT);
        }

        return builder.build();
    }

    default String buildFullName(String firstName, String lastName, String username) {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else if (username != null) {
            return username;
        }
        return "Unknown";
    }

    // Note: payment type is now sourced from Transaction when payment session is null,
    // and assumed PAYMENT when a payment session exists.

    default PaymentStatus parsePaymentStatus(String status) {
        if (status == null) return PaymentStatus.PENDING;
        try {
            return PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PaymentStatus.PENDING;
        }
    }
}
