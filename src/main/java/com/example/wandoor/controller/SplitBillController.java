package com.example.wandoor.controller;

import com.example.wandoor.model.request.SplitBillRequest;
import com.example.wandoor.model.response.SplitBillResponse;
import com.example.wandoor.service.SplitBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/split-bills")
public class SplitBillController {
    private final SplitBillService splitBillService;

    @PostMapping()
    public ResponseEntity<List<SplitBillResponse>> getSplitBills(
       @RequestBody SplitBillRequest request
    ) {
        List<SplitBillResponse> response = splitBillService.getSplitBills(request);
        return ResponseEntity.ok(response);
    }
}
