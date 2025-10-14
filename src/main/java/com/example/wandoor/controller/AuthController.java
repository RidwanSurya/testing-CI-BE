package com.example.wandoor.controller;

import com.example.wandoor.model.request.LoginRequest;
import com.example.wandoor.model.response.LoginResponse;
import com.example.wandoor.service.LoginOtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginOtpService loginOtpService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (
            @Valid @RequestBody LoginRequest req) {
        System.out.println("🔥 Login request masuk: " + req.userId());
        return ResponseEntity.ok(loginOtpService.login(req));
    }

//    @PostMapping("/verify/otp")

}
