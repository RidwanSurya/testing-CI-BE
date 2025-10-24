package com.example.wandoor.controller;

import com.example.wandoor.model.request.SplitBillDetailRequest;
import com.example.wandoor.model.response.SplitBillDetailResponse;
import com.example.wandoor.model.response.SplitBillsListResponse;
import com.example.wandoor.service.SplitBillService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/detail")
    public ResponseEntity<SplitBillDetailResponse> getAllSplitBillMember(@Valid @RequestBody SplitBillDetailRequest request){
        SplitBillDetailResponse response = splitBillService.getAllSplitBillMember(request);
        return ResponseEntity.ok(response);
    }
}
