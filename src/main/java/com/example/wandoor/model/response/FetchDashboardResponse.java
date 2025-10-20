package com.example.wandoor.model.response;

import com.example.wandoor.model.enums.AccountStatus;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RecordBuilder
public record FetchDashboardResponse(
        AssetOverview assetoverview,
        CashFlowOverview cashFlowOverview,
        List<PortfolioOverview> portfolioOverview,
        SplitBillOverview splitBillOverview,
        List<Accountlist> accountList

) {

    public record AssetOverview(
            BigDecimal totalAsset
            // timeDeposit + account + lifegoals + pension_funds
    ){}

    public record CashFlowOverview(
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal totalReceivable
    ){}

    public record PortfolioOverview(
            String productName,
            BigDecimal totalAmount
    ){}

    public record SplitBillOverview(
            Integer countSplitBill,
            BigDecimal totalBillAmount,
            BigDecimal remainingBillAmount
    ){}

    public record Accountlist(
            String accountNumber,
            String accountName,
            BigDecimal effectiveBalance,
//            String debitCardNumber,
            String accountProductName,
            AccountStatus accountStatus,
            LocalDateTime issuedTime
    ){}
}
