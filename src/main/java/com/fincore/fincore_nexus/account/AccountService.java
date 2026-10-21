package com.fincore.fincore_nexus.account;

import com.fincore.fincore_nexus.customer.Customer;
import com.fincore.fincore_nexus.customer.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.List;

@Service
public class AccountService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found"));

        BigDecimal initialBalance = request.initialDeposit() == null
                ? BigDecimal.ZERO
                : request.initialDeposit().setScale(2, RoundingMode.UNNECESSARY);

        Account account = new Account(generateAccountNumber(), customer, initialBalance);
        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public AccountResponse getById(Long id) {
        return AccountResponse.from(findAccount(id));
    }

    @Transactional(readOnly = true)
    public AccountResponse getByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account not found"));
        return AccountResponse.from(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> listByCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        return accountRepository.findByCustomerId(customerId).stream()
                .map(AccountResponse::from)
                .toList();
    }

    @Transactional
    public AccountResponse freeze(Long id) {
        return changeStatus(id, AccountStatus.FROZEN);
    }

    @Transactional
    public AccountResponse activate(Long id) {
        return changeStatus(id, AccountStatus.ACTIVE);
    }

    private AccountResponse changeStatus(Long id, AccountStatus newStatus) {
        Account account = findAccount(id);

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "A closed account cannot be changed");
        }
        if (account.getStatus() == newStatus) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Account is already " + newStatus);
        }

        account.setStatus(newStatus);
        return AccountResponse.from(account);
    }

    private Account findAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account not found"));
    }

    private String generateAccountNumber() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String candidate = String.valueOf(100_000_000_000L + RANDOM.nextLong(900_000_000_000L));
            if (!accountRepository.existsByAccountNumber(candidate)) {
                return candidate;
            }
        }
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Could not generate a unique account number");
    }
}