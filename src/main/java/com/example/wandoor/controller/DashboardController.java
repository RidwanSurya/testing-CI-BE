package com.example.wandoor.controller;

import com.example.wandoor.model.entity.TimeDepositAccount;
import com.example.wandoor.model.response.FetchDashboardResponse;
import com.example.wandoor.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class DashboardController {

    public final DashboardService dashboardService;

    @GetMapping("fetch-dashboard")
    public ResponseEntity<FetchDashboardResponse> fetchDashboard() {
        var response = dashboardService.fetchDashboard();
        return ResponseEntity.ok(response);
    }

}
