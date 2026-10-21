
package com.fincore.fincore_nexus.transaction;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts/{accountId}/transactions")
public class TransactionHistoryController {

    private final BankTransactionRepository transactionRepository;

    public TransactionHistoryController(
            BankTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public List<BankTransaction> getTransactionHistory(
            @PathVariable Long accountId) {
        return transactionRepository
                .findByAccountIdOrderByCreatedAtDesc(accountId);
    }
}
