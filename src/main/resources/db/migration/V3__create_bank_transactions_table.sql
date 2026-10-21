
CREATE TABLE bank_transactions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_id BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    balance_after NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bank_transactions_account
        FOREIGN KEY (account_id) REFERENCES accounts(id),

    CONSTRAINT chk_bank_transactions_type
        CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL')),

    CONSTRAINT chk_bank_transactions_amount
        CHECK (amount > 0),

    CONSTRAINT chk_bank_transactions_balance
        CHECK (balance_after >= 0)
);

CREATE INDEX idx_bank_transactions_account_created
    ON bank_transactions (account_id, created_at DESC);
