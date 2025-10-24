package com.example.wandoor.controller;

import com.example.wandoor.model.request.AddNewSplitBillRequest;
import com.example.wandoor.model.response.SplitBillsListResponse;
import com.example.wandoor.service.SplitBillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("api/split-bill")
@RequiredArgsConstructor
@Log4j2
public class SplitBillController {
    private final SplitBillService splitBillService;

    @GetMapping
    public ResponseEntity<SplitBillsListResponse> getAllSplitBills(){
        SplitBillsListResponse response = splitBillService.getAllSplitBill();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> createSplitBill(
            @Valid @RequestBody AddNewSplitBillRequest request){

        log.info("Receive create split bill request: {}", request.splitBillTitle());
        String splitBillId = splitBillService.createSplitBill(request);

        return ResponseEntity
                .created(URI.create("api/split-bill" + splitBillId))
                .body(Map.of(
                        "splitBillId", splitBillId,
                        "message", "Split bill created successfully"
                ));
    }
}
