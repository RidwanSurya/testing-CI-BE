package com.example.wandoor.service;

import com.example.wandoor.model.entity.OtpVerification;
import com.example.wandoor.model.entity.UserAuth;
import com.example.wandoor.model.request.LoginRequest;
import com.example.wandoor.model.response.LoginResponse;
import com.example.wandoor.repository.UserAuthRepository;
import com.example.wandoor.repository.UserOtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class LoginOtpService{
    private final UserAuthRepository userAuthRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserOtpVerificationRepository userOtpVerificationRepository;
    private final EmailService emailService;


    public LoginResponse login (LoginRequest req){
        // any userId in db?
        var userAuth = userAuthRepository.findByUserId(req.userId());

        // cek apakah userAuth kosong
        if (userAuth.isEmpty()){
            return new LoginResponse(false, "User Not Found", null);
        }

        // cek password
//        var checkPassword = passwordEncoder.matches(req.password(), userAuth.get().getPassword());
        var checkPassword = req.password().equals(userAuth.get().getPassword());
        if (!checkPassword) {
            return new LoginResponse(false, "Invalid Credential", null);
        }

        // generate OTP
        var otp = String.format("%06d", new Random().nextInt(999999));
        var otpReff = UUID.randomUUID().toString();

        // save to table OtpVerification
        var otpVerification = OtpVerification.builder()
                .id(UUID.randomUUID().toString())
                .userId(userAuth.get().getUserId())
                .otpCode(otp)
                .emailTo(userAuth.get().getEmailAddress())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .createdTime(LocalDateTime.now())
                .build();
        userOtpVerificationRepository.save(otpVerification);

        // sent OTP by email
        emailService.sendOtp(userAuth.get().getEmailAddress(), otp);

        return new LoginResponse(true, "OTP dikirim", otpReff);
    }
}

