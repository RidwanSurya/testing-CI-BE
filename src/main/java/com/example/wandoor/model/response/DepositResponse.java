package com.example.wandoor.model.response;
import java.math.BigDecimal;
import java.util.List;

public record DepositResponse(
    boolean status,
    String message,
    DepositData data
) {
    public record DepositData(
        String fund_id,
        String title,
        BigDecimal total_balance,
        Integer count_accounts,
        List<Items> items
    ) {}

    public record Items(
        String item_id,
        String deposit_account_number,
        BigDecimal balance,
        Integer tenor_months,
        String maturity_date,
        BigDecimal interest_rate,
        String status
    ) {}
}
