package com.example.wandoor.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.wandoor.config.RequestContext;
import com.example.wandoor.model.entity.Account;
import com.example.wandoor.model.entity.LifegoalsAccount;
import com.example.wandoor.model.entity.TimeDepositAccount;
import com.example.wandoor.model.enums.AccountStatus;
import com.example.wandoor.model.enums.DebitCredit;
import com.example.wandoor.model.response.FetchDashboardResponse;
import com.example.wandoor.repository.AccountRepository;
import com.example.wandoor.repository.DplkAccountRepository;
import com.example.wandoor.repository.LifegoalsAccountRepository;
import com.example.wandoor.repository.ProfileRepository;
import com.example.wandoor.repository.SplitBillMemberRepository;
import com.example.wandoor.repository.SplitBillRepository;
import com.example.wandoor.repository.TimeDepositRepository;
import com.example.wandoor.repository.TrxHistoryRepository;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

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
    private final DplkAccountRepository dplkAccountRepository;

    @Transactional
    public FetchDashboardResponse fetchDashboard() {
        var userId = RequestContext.get().getUserId();
        var cif = RequestContext.get().getCif(); // tetap digunakan untuk repositori lain

        // 🔹 Verifikasi user
        profileRepository.findByIdAndCif(userId, cif)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 🔹 Ambil data rekening & investasi
        var timeDeposit = timeDepositRepository.findByUserIdAndCif(userId, cif);
        var account = accountRepository.findByUserIdAndCif(userId, cif);
        var lifegoals = lifegoalsAccountRepository.findByUserIdAndCif(userId, cif);
        var dplk = dplkAccountRepository.findAllByUserIdAndCif(userId, cif);

        var statuses = List.of(AccountStatus.BUKA, AccountStatus.BARU);
        List<Account> accounts = accountRepository.fetchActiveAccounts(userId, cif, statuses);

        var totalTimeDeposit = sum(timeDeposit, TimeDepositAccount::getEffectiveBalance);
        var totalAccount = sum(account, Account::getEffectiveBalance);
        var totalLifegoals = sum(lifegoals, LifegoalsAccount::getEstimationAmount);
        // var totalDplk = sum(dplk, DplkAccount::getBalance);

        var totalAsset = totalAccount.add(totalTimeDeposit).add(totalLifegoals); // bisa ditambah dplk jika siap

        // 🔹 Split bill overview
        var countSplitBills = splitBillRepository.countActiveByCreator(userId, cif);
        var totalBillAmount = nvl(splitBillRepository.sumTotalBillByCreator(userId, cif));
        var remainingBillAmount = nvl(splitBillMemberRepository.sumRemainingForCreator(userId, cif));

        // 🔹 Cash flow overview — TANPA CIF (karena field-nya tidak ada di TrxHistory)
        var totalIncome = nvl(trxHistoryRepository.sumTransactionAmountByUserIdAndDebitCredit(userId, DebitCredit.D));
        var totalExpenses = nvl(trxHistoryRepository.sumTransactionAmountByUserIdAndDebitCredit(userId, DebitCredit.C));

        // 🔹 Portfolio Overview
        var portfolioOverview = List.of(
                new FetchDashboardResponse.PortfolioOverview("timeDeposit", totalTimeDeposit),
                new FetchDashboardResponse.PortfolioOverview("lifegoals", totalLifegoals),
                new FetchDashboardResponse.PortfolioOverview("accountSavings", totalAccount)
        );

        // 🔹 Account list overview
        var accountList = accounts.stream()
                .map(a -> new FetchDashboardResponse.Accountlist(
                        a.getAccountNumber(),
                        a.getAccountHolderName(),
                        a.getEffectiveBalance(),
                        a.getSubCat(),
                        a.getAccountStatus(),
                        a.getCreatedTime()
                ))
                .toList();

        // 🔹 Response akhir
        return new FetchDashboardResponse(
                new FetchDashboardResponse.AssetOverview(totalAsset),
                new FetchDashboardResponse.CashFlowOverview(totalIncome, totalExpenses, remainingBillAmount),
                portfolioOverview,
                new FetchDashboardResponse.SplitBillOverview((int) countSplitBills, totalBillAmount, remainingBillAmount),
                accountList
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
