package com.example.wandoor.controller;

import com.example.wandoor.model.request.LoginRequest;
import com.example.wandoor.model.request.VerifyOtpRequest;
import com.example.wandoor.model.response.LoginResponse;
import com.example.wandoor.model.response.LogoutResponse;
import com.example.wandoor.model.response.VerifyOtpResponse;
import com.example.wandoor.service.LoginOtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginOtpService loginOtpService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (
            @Valid @RequestBody LoginRequest req) {
//        System.out.println("🔥 Login request masuk: " +
//        req.username()));
        return ResponseEntity.ok(loginOtpService.login(req));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOtpResponse> verifyOtp (
            @Valid @RequestBody VerifyOtpRequest req){
//        System.out.println("otp");
        return ResponseEntity.ok(loginOtpService.verifyOtp(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout (
            @RequestHeader("Authorization") String auth,
            @RequestHeader("User-Id") String userId
    ){
        String token = auth.replace("Bearer ", "");
        return ResponseEntity.ok(loginOtpService.logout(token, userId));
    }

}
