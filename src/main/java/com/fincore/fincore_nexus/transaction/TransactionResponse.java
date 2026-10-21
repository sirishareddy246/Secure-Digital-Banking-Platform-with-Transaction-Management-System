
package com.fincore.fincore_nexus.transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        Long id,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal balanceAfter,
        Instant createdAt
) {
    public static TransactionResponse from(BankTransaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getCreatedAt()
        );
    }
}
