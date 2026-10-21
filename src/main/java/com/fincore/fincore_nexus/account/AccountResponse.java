package com.fincore.fincore_nexus.account;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        Long id,
        String accountNumber,
        Long customerId,
        String customerName,
        BigDecimal balance,
        AccountStatus status,
        Instant createdAt
) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getCustomer().getId(),
                account.getCustomer().getFullName(),
                account.getBalance(),
                account.getStatus(),
                account.getCreatedAt()
        );
    }
}