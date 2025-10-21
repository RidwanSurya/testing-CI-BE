package com.example.wandoor.controller;

import com.example.wandoor.model.response.SplitBillsListResponse;
import com.example.wandoor.service.SplitBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/split-bill")
@RequiredArgsConstructor
public class SplitBillController {
    private final SplitBillService splitBillService;

    @GetMapping
    public ResponseEntity<SplitBillsListResponse> getAllSplitBills(){
        SplitBillsListResponse response = splitBillService.getAllSplitBill();
        return ResponseEntity.ok(response);
    }
}
