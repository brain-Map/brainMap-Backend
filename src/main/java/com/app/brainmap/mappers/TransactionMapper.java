package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.TransactionDto;
import com.app.brainmap.domain.entities.PaymentSession;
import com.app.brainmap.domain.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

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
}

