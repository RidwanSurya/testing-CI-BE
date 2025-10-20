package com.example.wandoor.controller;


import com.example.wandoor.model.request.LifegoalsDetailsRequest;
import com.example.wandoor.model.response.LifegoalsDetailsResponse;
import com.example.wandoor.model.response.LifegoalsGroupResponse;
import com.example.wandoor.model.response.LifegoalsListResponse;
import com.example.wandoor.model.response.LifegoalsResponse;
import com.example.wandoor.service.LifegoalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class LifegoalsController {
    private final LifegoalsService lifegoalsService;

    @GetMapping("lifegoals")
    public ResponseEntity<Map<String, LifegoalsGroupResponse>> fetchAllLifegoals(){
        Map<String, LifegoalsGroupResponse> response = lifegoalsService.fetchAllLifegoals();
        return ResponseEntity.ok(response);
    }

    @PostMapping("lifegoals-detail")
    public ResponseEntity<LifegoalsDetailsResponse> fetchDetailLifegoals(
            @RequestBody LifegoalsDetailsRequest req
            ){
        var response = lifegoalsService.fetchDetailLifegoals(req);
        return ResponseEntity.ok(response);
    }
}
