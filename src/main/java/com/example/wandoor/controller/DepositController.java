package com.example.wandoor.controller;

import com.example.wandoor.model.response.DepositResponse;
import com.example.wandoor.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deposit")
@RequiredArgsConstructor // ✅ otomatis membuat constructor untuk field final
public class DepositController {

    private final DepositService depositService;

    @GetMapping("/{userId}")
    public ResponseEntity<DepositResponse> fetchDeposit(
            Authentication auth,
            @RequestHeader("cif") String cif,
            @PathVariable String userId
    ) {
        // ✅ Ambil userId dari token (JWT principal)
        String userIdHeader = (String) auth.getPrincipal();

        // ✅ Panggil service dan kembalikan respons JSON
        DepositResponse response = depositService.fetchDeposit(userIdHeader, cif, userId);
        return ResponseEntity.ok(response);
    }
}
