package com.example.wandoor.controller;


import com.example.wandoor.model.response.DplkListResponse;
import com.example.wandoor.service.DplkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class DplkController {

    private final DplkService dplkService;

    @GetMapping("dplk")
    public ResponseEntity<DplkListResponse> fetchDplkData(){
        var response = dplkService.fetchDplkData();
        return ResponseEntity.ok(response);
    }
}
