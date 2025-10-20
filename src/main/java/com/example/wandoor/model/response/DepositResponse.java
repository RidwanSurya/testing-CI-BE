package com.example.wandoor.model.response;

import java.time.LocalDateTime;

public record DepositResponse(
    boolean status,
    String message,
    DepositData data
) {
    public record DepositData(
        String fundId,
        String title,
        Integer totalBalance,
        Integer countAccounts,
        java.util.List<Items> items
    ) {}

    public record Items(
        String itemId,
        String depositAccountNumber,
        Integer balance,
        Integer tenorMonths,
        LocalDateTime maturityDate,
        Double interestRate,
        String status
    ) {}
}
