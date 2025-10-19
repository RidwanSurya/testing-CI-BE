package com.example.wandoor.service;

import com.example.wandoor.config.RequestContext;
import com.example.wandoor.model.entity.LifegoalsAccount;
import com.example.wandoor.model.response.LifegoalsListResponse;
import com.example.wandoor.model.response.LifegoalsResponse;
import com.example.wandoor.repository.LifegoalsAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class LifegoalsService {

    private final LifegoalsAccountRepository lifegoalsAccountRepository;

    @Transactional(readOnly = true)
    public LifegoalsListResponse fetchAllLifegoals(){
        var userId = RequestContext.get().getUserId();
        var cif = RequestContext.get().getCif();

        var rows = lifegoalsAccountRepository.findByUserIdAndCif(userId, cif);
        if (rows.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "data user not found");
        }

        rows = rows.stream()
                .sorted(Comparator.comparing(LifegoalsAccount::getLifegoalsCategoryName, Comparator.nullsLast(String::compareTo))
                        .thenComparing(LifegoalsAccount::getLifegoalsName))
                .toList();

        var items = rows.stream().map(a -> {
            var  current = nz(a.getEstimationAmount());
            var target = nz(a.getAccountTargetAmount());
            var pict = pct(current, target);

            var goalId = a.getLifegoalsTrxCreationId() != null ? a.getLifegoalsTrxCreationId() : a.getId();

            return new LifegoalsListResponse.Item(
                    goalId,
                    a.getLifegoalsName(),
                    a.getLifegoalsDescription(),
                    current,
                    target,
                    pict
            );
        }).toList();

        return new LifegoalsListResponse("true", "Life goal detail fetched successfully", items);
    }
    private static BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }

    private static int pct(BigDecimal current, BigDecimal target) {
        if (target == null || target.signum() == 0) return 0;
        return current.multiply(BigDecimal.valueOf(100))
                .divide(target, 0, RoundingMode.DOWN)
                .max(BigDecimal.ZERO)
                .min(BigDecimal.valueOf(100))
                .intValue();
    }

}
