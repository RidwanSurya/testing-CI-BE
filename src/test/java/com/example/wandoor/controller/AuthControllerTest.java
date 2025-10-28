//package com.example.wandoor.controller;
//
//import com.example.wandoor.model.request.LoginRequest;
//import com.example.wandoor.model.request.VerifyOtpRequest;
//import com.example.wandoor.model.response.LoginResponse;
//import com.example.wandoor.model.response.VerifyOtpResponse;
//import com.example.wandoor.service.LoginOtpService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//public class AuthControllerTest {
//    private final LoginOtpService loginOtpService;
//
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
//        LoginResponse response = loginOtpService.login(request);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/verify-otp")
//    public ResponseEntity<VerifyOtpResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
//        VerifyOtpResponse response = loginOtpService.verifyOtp(request);
//        return ResponseEntity.ok(response);
//    }
//}
