package com.example.wandoor.controller;

import com.example.wandoor.model.request.TransactionHistoryRequest;
import com.example.wandoor.model.response.TransactionHistoryResponse;
import com.example.wandoor.service.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class TrxHistoryController {

    private final TransactionHistoryService service;

    @PostMapping("/trx-history")
    ResponseEntity<TransactionHistoryResponse> fetchTransactionHistory(TransactionHistoryRequest request){
        var response = service.fetchTransactionHistory(request);
        return ResponseEntity.ok(response);
    }
}