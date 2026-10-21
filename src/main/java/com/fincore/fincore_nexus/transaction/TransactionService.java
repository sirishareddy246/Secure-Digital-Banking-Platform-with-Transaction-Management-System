
package com.fincore.fincore_nexus.transaction;

import com.fincore.fincore_nexus.account.Account;
import com.fincore.fincore_nexus.account.AccountRepository;
import com.fincore.fincore_nexus.account.AccountStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final BankTransactionRepository transactionRepository;

    public TransactionService(
            AccountRepository accountRepository,
            BankTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public BankTransaction deposit(Long accountId, TransactionRequest request) {
        Account account = findActiveAccount(accountId);
        BigDecimal amount = validateAmount(request.amount());

        account.deposit(amount);
        accountRepository.save(account);

        BankTransaction transaction = new BankTransaction(
                account,
                TransactionType.DEPOSIT,
                amount,
                account.getBalance()
        );

        return transactionRepository.save(transaction);
    }

    @Transactional
    public BankTransaction withdraw(Long accountId, TransactionRequest request) {
        Account account = findActiveAccount(accountId);
        BigDecimal amount = validateAmount(request.amount());

        try {
            account.withdraw(amount);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        accountRepository.save(account);

        BankTransaction transaction = new BankTransaction(
                account,
                TransactionType.WITHDRAWAL,
                amount,
                account.getBalance()
        );

        return transactionRepository.save(transaction);
    }

    private Account findActiveAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account not found"));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Account is not active");
        }

        return account;
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Amount must be greater than zero");
        }

        try {
            return amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Amount cannot have more than 2 decimal places");
        }
    }
}
