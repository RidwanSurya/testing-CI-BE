package com.example.wandoor.model.response;
import java.math.BigDecimal;
import java.util.List;

public record DepositResponse(
    boolean status,
    String message,
    DepositData data
) {
    public record DepositData(
        String fundId,
        String title,
        BigDecimal totalBalance,
        Integer countAccounts,
        List<Items> items
    ) {}

    public record Items(
        String itemId,
        String depositAccountNumber,
        BigDecimal balance,
        Integer tenorMonths,
        String maturityDate,
        BigDecimal interestRate,
        String status
    ) {}
}
