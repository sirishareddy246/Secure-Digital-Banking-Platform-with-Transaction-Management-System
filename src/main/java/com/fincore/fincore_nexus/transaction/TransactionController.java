
package com.fincore.fincore_nexus.transaction;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts/{accountId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public BankTransaction deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody TransactionRequest request) {
        return transactionService.deposit(accountId, request);
    }

    @PostMapping("/withdraw")
    public BankTransaction withdraw(
            @PathVariable Long accountId,
            @Valid @RequestBody TransactionRequest request) {
        return transactionService.withdraw(accountId, request);
    }
}
