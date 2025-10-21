package com.example.wandoor.service;

import com.example.wandoor.config.RequestContext;
import com.example.wandoor.model.entity.TimeDepositAccount;
import com.example.wandoor.model.response.DepositResponse;
import com.example.wandoor.repository.DepositRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class DepositService {

    private final DepositRepository depositRepository;
    
    public DepositResponse fetchDeposit(){
        var userId = RequestContext.get().getUserId();
        var cif = RequestContext.get().getCif();

        // logic - getAllList by Id
        List<TimeDepositAccount> deposits = depositRepository.findByUserIdAndCif(userId, cif);
        if (deposits.isEmpty()) {
            throw new RuntimeException("Deposit dengan ID " + userId + " tidak ditemukan");
        }

        var fund_id = "AGG_TIMEDEPOSITS_USR001 -> Ini dapet dari mana??";
        var title = "Time Deposits -> ini juga dapet dari mana?";
        // ✅ Hitung total balance dan jumlah akun
        BigDecimal totalBalance = deposits.stream()
                .map(td -> td.getEffectiveBalance() == null ? BigDecimal.ZERO : td.getEffectiveBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var countAccounts = deposits.size();

        // ✅ Mapping entity → response item
        List<DepositResponse.Items> items = new ArrayList<>();
        for (TimeDepositAccount d : deposits) {
            items.add(new DepositResponse.Items(
                    d.getId(),
                    d.getDepositAccountNumber(),
                    d.getEffectiveBalance() == null ? BigDecimal.ZERO : d.getEffectiveBalance(),
                    d.getTenorMonths(),
                    d.getMaturityDate() == null
                            ? null
                            : d.getMaturityDate().atOffset(ZoneOffset.UTC).toString(),
                    d.getInterestRate(),
                    d.getDepositAccountStatus()
            ));
        }

        // ✅ Bungkus ke dalam DepositData
        DepositResponse.DepositData data = new DepositResponse.DepositData(
                fund_id,
                title,
                totalBalance,
                countAccounts,
                items
        );

        // ✅ Return response lengkap
        return new DepositResponse(
                true,
                "Time deposits fetched successfully",
                data
        );
    }
}