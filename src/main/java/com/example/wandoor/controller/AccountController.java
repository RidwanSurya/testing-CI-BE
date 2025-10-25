package com.example.wandoor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.wandoor.model.request.AccountRequest;
import com.example.wandoor.model.response.AccountResponse;
import com.example.wandoor.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/savings")
    public ResponseEntity<AccountResponse> getAccountData(@RequestBody(required = false) AccountRequest request) {
        var response = accountService.dataAccount(request);
        return ResponseEntity.ok(response);
    }
}
