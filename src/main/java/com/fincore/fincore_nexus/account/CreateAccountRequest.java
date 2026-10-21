package com.fincore.fincore_nexus.account;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotNull(message = "Customer id is required")
        Long customerId,

        @DecimalMin(value = "0.00", message = "Initial deposit cannot be negative")
        @Digits(integer = 17, fraction = 2,
                message = "Initial deposit can have at most 2 decimal places")
        BigDecimal initialDeposit
) {
}