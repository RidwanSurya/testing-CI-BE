package com.example.wandoor.controller;

import com.example.wandoor.model.response.DepositResponse;
import com.example.wandoor.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor // ✅ otomatis membuat constructor untuk field final
public class DepositController {

    private final DepositService depositService;

    @GetMapping("/detail-deposit")
    public ResponseEntity<DepositResponse> fetchDeposit(
    ) {
        DepositResponse response = depositService.fetchDeposit();
        return ResponseEntity.ok(response);
    }
}
