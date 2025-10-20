package com.example.wandoor.service;

import com.example.wandoor.model.entity.Account;
import com.example.wandoor.model.entity.LifegoalsAccount;
import com.example.wandoor.model.entity.SplitBill;
import com.example.wandoor.model.entity.TimeDepositAccount;
import com.example.wandoor.model.enums.TrxType;
import com.example.wandoor.model.response.FetchDashboardResponse;
import com.example.wandoor.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.Long.sum;

@Service
@Log4j2
@AllArgsConstructor
public class DashboardService {

    private final ProfileRepository profileRepository;
    private final TimeDepositRepository timeDepositRepository;
    private final AccountRepository accountRepository;
    private final LifegoalsAccountRepository lifegoalsAccountRepository;
    private final SplitBillRepository splitBillRepository;
    private final TrxHistoryRepository trxHistoryRepository;
    private final SplitBillMemberRepository splitBillMemberRepository;

    @Transactional
    public FetchDashboardResponse fetchDashboard(String userId, String cif) {
        // timeDeposit + account + lifegoals + pension_funds
        var userExists = profileRepository.findByCif(cif)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PROFILE TIDAK DITEMUKAN"));
//        if (!userExists){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
//        }

        var timeDeposit = timeDepositRepository.findByUserIdAndCif(userId,  cif);
        var account = accountRepository.findByUserIdAndCif(userId, cif);
        var lifegoals = lifegoalsAccountRepository.findByUserIdAndCif(userId, cif);
        // add pensiun
        var accounts = accountRepository.findByUserIdAndCif(userId, cif);

        var totalTimeDeposit = sum(timeDeposit, TimeDepositAccount::getEffectiveBalance);
        var totalAccount = sum(account, Account::getEffectiveBalance);
        var totalLifegoals = sum(lifegoals, LifegoalsAccount::getEstimationAmount);
        // total pensiun

        var totalAsset = totalAccount.add(totalTimeDeposit).add(totalLifegoals);
        // add total pensiun

        // Split bill overview
        var countSplitBills = splitBillRepository.countActiveByCreator(userId, cif);
        var totalBillAmount = nvl(splitBillRepository.sumTotalBillByCreator(userId, cif));
        var remainingBillAmount = nvl(splitBillMemberRepository.sumRemainingForCreator(userId, cif));

        // CASH FLOW OVERVIEW
        var totalIncome = nvl(trxHistoryRepository.sumTrxAmountByUserIdAndCifAndTrxType(userId, TrxType.CREDIT));
        var totalExpenses = nvl(trxHistoryRepository.sumTrxAmountByUserIdAndCifAndTrxType(userId, TrxType.DEBIT));

        // Portfolio Overview
        var portfolioOverview = List.of(
                new FetchDashboardResponse.PortfolioOverview("timeDeposit", totalTimeDeposit),
                new FetchDashboardResponse.PortfolioOverview("lifegoals", totalLifegoals),
                new FetchDashboardResponse.PortfolioOverview("accountSavings", totalAccount)
                // kurang dplk
        );

        // AccountList
        var accountlist = accounts.stream()
                .map(a -> new FetchDashboardResponse.Accountlist(
                        a.getAccountNumber(),
                        a.getAccountHolderName(),
                        a.getEffectiveBalance(),
                        a.getSubCat(),
                        a.getAccountStatus(),
                        null
                )).toList();


        return new FetchDashboardResponse(
                new FetchDashboardResponse.AssetOverview(totalAsset),
                new FetchDashboardResponse.CashFlowOverview(totalIncome, totalExpenses, remainingBillAmount),
                portfolioOverview,
                new FetchDashboardResponse.SplitBillOverview((int)countSplitBills, totalBillAmount, remainingBillAmount),
                accountlist
        );

    }

    private static <T> BigDecimal sum(List<T> list, java.util.function.Function<T, BigDecimal> getter) {
        return list == null ? BigDecimal.ZERO :
                list.stream()
                        .map(it -> {
                            BigDecimal v = getter.apply(it);
                            return v == null ? BigDecimal.ZERO : v;
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

}



