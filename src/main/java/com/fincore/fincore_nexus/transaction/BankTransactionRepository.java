
package com.fincore.fincore_nexus.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankTransactionRepository
        extends JpaRepository<BankTransaction, Long> {

    List<BankTransaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);
}

