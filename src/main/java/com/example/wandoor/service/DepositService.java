package com.example.wandoor.service;

import com.example.wandoor.model.entity.TimeDepositAccount;
import com.example.wandoor.model.response.DepositResponse;
import com.example.wandoor.repository.DepositRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    
    public DepositResponse fetchDeposit(String userIdHeader, String cif, String userId){
        // logic - getAllList by Id
        List<TimeDepositAccount> deposits = depositRepository.getDepositByUserId(userId);
        if (deposits.isEmpty()) {
            throw new RuntimeException("Deposit dengan ID " + userId + " tidak ditemukan");
        }

        var fund_id = "AGG_TIMEDEPOSITS_USR001 -> Ini dapet dari mana??";
        var title = "Time Deposits -> ini juga dapet dari mana?";
        // ✅ Hitung total balance dan jumlah akun
        int totalBalance = 0;
        for (TimeDepositAccount deposit : deposits) {
            totalBalance += deposit.getEffectiveBalance();
        }


        var countAccounts = deposits.size();

        // ✅ Mapping entity → response item
        List<DepositResponse.Items> items = new ArrayList<>();
        for (TimeDepositAccount d : deposits) {
            items.add(new DepositResponse.Items(
                    d.getId(),
                    d.getDepositAccountNumber(),
                    d.getEffectiveBalance(),
                    d.getTenorMonths(),
                    d.getMaturityDate(),
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