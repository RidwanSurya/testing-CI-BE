package com.example.wandoor.controller;


import com.example.wandoor.model.response.LifegoalsListResponse;
import com.example.wandoor.model.response.LifegoalsResponse;
import com.example.wandoor.service.LifegoalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class LifegoalsController {
    private final LifegoalsService lifegoalsService;

    @GetMapping("lifegoals")
    public ResponseEntity<LifegoalsListResponse> fetchAllLifegoals(){
        var response = lifegoalsService.fetchAllLifegoals();
        return ResponseEntity.ok(response);
    }
}
