package com.example.wandoor.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.wandoor.model.entity.OtpVerification;
import com.example.wandoor.model.entity.RoleManagement;
import com.example.wandoor.model.request.LoginRequest;
import com.example.wandoor.model.request.VerifyOtpRequest;
import com.example.wandoor.model.response.LoginResponse;
import com.example.wandoor.model.response.VerifyOtpResponse;
import com.example.wandoor.repository.ProfileRepository;
import com.example.wandoor.repository.RoleManagementRepository;
import com.example.wandoor.repository.UserAuthRepository;
import com.example.wandoor.repository.UserOtpVerificationRepository;
import com.example.wandoor.util.Helpers;
<<<<<<< HEAD
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
=======
import com.example.wandoor.util.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
>>>>>>> 7e1522fda944f3965aa4bbae49600366c2163b09

@Service
@Log4j2
@RequiredArgsConstructor
public class LoginOtpService{
    private final UserAuthRepository userAuthRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserOtpVerificationRepository userOtpVerificationRepository;
    private final EmailService emailService;
    private final Helpers helpers;
    private final ProfileRepository profileRepository;
    private final RoleManagementRepository roleManagementRepository;


    public LoginResponse login (LoginRequest req){
        // any userId in db?
        var userAuth = userAuthRepository.findByUserId(req.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UserId atau password salah"));

        // cek apakah userAuth di block
        if (userAuth.getIsUserBlocked() != null && userAuth.getIsUserBlocked() == 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akun diblokir");
        }

        //  password verification
<<<<<<< HEAD
//        var checkPassword = passwordEncoder.matches(req.password(), userAuth.get().getPassword());
=======
        // var checkPassword = passwordEncoder.matches(req.password(), userAuth.getPassword());
>>>>>>> 7e1522fda944f3965aa4bbae49600366c2163b09
        var checkPassword = req.password().equals(userAuth.getPassword());
        if (!checkPassword) {
            return new LoginResponse(false, "Invalid Credential", null);
        }

        // get email form profile
        var profile = profileRepository.findById(userAuth.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile tidak ditemukan"));
//        var emailTo = userAuth.getEmailAddress() != null ? userAuth.getEmailAddress() ? profile.getEmailAddress();

        // generate OTP
        var otp = String.format("%06d", new Random().nextInt(999999));
        var otpReff = UUID.randomUUID().toString();

        // save to table OtpVerification
        var otpVerification = OtpVerification.builder()
                .id(otpReff)
                .userId(userAuth.getUserId())
                .otpCode(otp)
                .emailTo(userAuth.getEmailAddress())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isUsed(0)
                .createdTime(LocalDateTime.now())
                .build();
        userOtpVerificationRepository.save(otpVerification);

        // sent OTP by email
        emailService.sendOtp(userAuth.getEmailAddress(), otp);

        return new LoginResponse(true, "Kode OTP telah dikirim", otpReff);
    }

    public VerifyOtpResponse verifyOtp(VerifyOtpRequest req) {
        var ref = UUID.fromString(req.otpRef());
        // get row otp
        var otpRow = userOtpVerificationRepository.findByIdAndIsUsed(req.otpRef(), 0)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP tidak valid"));

        // cek expired
        if (otpRow.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }

        // compare otp input and db
        if (!helpers.safeEquals(otpRow.getOtpCode(), req.otp())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "OTP Salah");
        }

        // used and make jwt
        otpRow.setIsUsed(1);

        var role = roleManagementRepository.findFirstByUserId(otpRow.getUserId())
                        .map(RoleManagement::getRollName).orElse("Nasabah");

        userOtpVerificationRepository.save(otpRow);

        var jwt = helpers.issuejwt(otpRow.getUserId(), role);

        return new VerifyOtpResponse(true, "login berhasil", jwt, null);
    }
}