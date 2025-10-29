//package com.example.wandoor.controller;
//
//import com.example.wandoor.model.request.LoginRequest;
//import com.example.wandoor.model.request.ResendOtpRequest;
//import com.example.wandoor.model.request.VerifyOtpRequest;
//import com.example.wandoor.model.response.LoginResponse;
//import com.example.wandoor.model.response.LogoutResponse;
//import com.example.wandoor.model.response.ResendOtpResponse;
//import com.example.wandoor.model.response.VerifyOtpResponse;
//import com.example.wandoor.security.JwtAuthenticationFilter;
//import com.example.wandoor.service.LoginOtpService;
//import com.example.wandoor.util.JwtUtils;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(controllers = AuthController.class,
//        excludeAutoConfiguration = {
//                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
//                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
//        })
//@AutoConfigureMockMvc
//class AuthControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    // Mock semua bean yang dipakai di AuthController
//    @Mock
//    private LoginOtpService loginOtpService;
//
//    @Mock
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    @Mock
//    private JwtUtils jwtUtils;
//
//    @Mock
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Test
//    void testLogin_success() throws Exception {
//        var req = new LoginRequest("oktaviaqa", "123456");
//        var mockResponse = new LoginResponse(true, "Kode OTP telah dikirim ke email Anda", "session-123");
//
//        Mockito.when(loginOtpService.login(any(LoginRequest.class))).thenReturn(mockResponse);
//
//        mockMvc.perform(post("/api/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(true))
//                .andExpect(jsonPath("$.message").value("Kode OTP telah dikirim ke email Anda"))
//                .andExpect(jsonPath("$.token").value("token-abc"))
//                .andExpect(jsonPath("$.user.username").value("oktaviaqa"));
//    }
//
//    @Test
//    void testLogin_blockedUser() throws Exception {
//        var req = new LoginRequest("blockedUser", "wrongpass");
//
//        Mockito.when(loginOtpService.login(any(LoginRequest.class)))
//                .thenThrow(new org.springframework.web.server.ResponseStatusException(
//                        org.springframework.http.HttpStatus.FORBIDDEN,
//                        "Akun diblokir, hubungi CS untuk membuka blokir."
//                ));
//
//        mockMvc.perform(post("/api/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isForbidden())
//                .andExpect(status().reason("Akun diblokir, hubungi CS untuk membuka blokir."));
//    }
//
//    @Test
//    void testVerifyOtp_success() throws Exception {
//        var req = new VerifyOtpRequest("session-123", "654321");
//        var mockResponse = new VerifyOtpResponse(true, "login berhasil", "token-abc",
//                new VerifyOtpResponse.User("user-1", "oktaviaqa", "Nasabah"), 1);
//
//        Mockito.when(loginOtpService.verifyOtp(any(VerifyOtpRequest.class))).thenReturn(mockResponse);
//
//        mockMvc.perform(post("/api/auth/verify-otp")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(true))
//                .andExpect(jsonPath("$.message").value("login berhasil"))
//                .andExpect(jsonPath("$.token").value("token-abc"))
//                .andExpect(jsonPath("$.user.username").value("oktaviaqa"));
//    }
//
//    @Test
//    void testResendOtp_success() throws Exception {
//        var req = new ResendOtpRequest("session-123");
//        var mockResponse = new ResendOtpResponse(true,
//                "Kode OTP baru telah dikirim ke email Anda. Resend ke-1 dari 3.", 0);
//
//        Mockito.when(loginOtpService.resendOtp(any(ResendOtpRequest.class))).thenReturn(mockResponse);
//
//        mockMvc.perform(post("/api/auth/resend-otp")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(true))
//                .andExpect(jsonPath("$.message").value("Kode OTP baru telah dikirim ke email Anda. Resend ke-1 dari 3."));
//    }
//
//    @Test
//    void testLogout_success() throws Exception {
//        var mockResponse = new LogoutResponse(true, "logout berhasil");
//
//        Mockito.when(loginOtpService.logout(any(String.class), any(String.class))).thenReturn(mockResponse);
//
//        mockMvc.perform(post("/api/auth/logout")
//                        .header("Authorization", "Bearer token-abc")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"userId\":\"user-1\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(true))
//                .andExpect(jsonPath("$.message").value("logout berhasil"));
//    }
//}
