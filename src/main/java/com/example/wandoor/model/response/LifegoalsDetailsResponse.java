package com.example.wandoor.model.response;

import java.math.BigDecimal;

public record LifegoalsDetailsResponse(
        String accountNumber,
        BigDecimal estimatedAccumulateBalance,
        BigDecimal initialDeposit,
        BigDecimal interestRate,
        String maturityDate,
        String createdTime,
        Integer lifegoalsDuration,
        String disbursmentAccountNumber
) {
}
