package com.example.wandoor.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import com.example.wandoor.model.response.LogoutResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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
import com.example.wandoor.util.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class LoginOtpService{
    private final UserAuthRepository userAuthRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(5);
    private final UserOtpVerificationRepository userOtpVerificationRepository;
    private final EmailService emailService;
    private final Helpers helpers;
    private final ProfileRepository profileRepository;
    private final RoleManagementRepository roleManagementRepository;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final BlockUserNow blockUserNow;

    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final Duration ISSUE_WINDOW = Duration.ofMinutes(10);
    private static final int ISSUE_LIMIT = 3;
    private static final Duration TOKEN_TTL = Duration.ofHours(2);


    @Transactional
    public LoginResponse login (LoginRequest req){
        var userAuth = userAuthRepository.findByUsername(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username atau Password salah"));

        if (userAuth.getIsUserBlocked() != null && Integer.valueOf(1).equals(userAuth.getIsUserBlocked())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akun diblokir, hubungi CS untuk membuka blokir.");
        }

        var checkPassword = passwordEncoder.matches(req.password(), userAuth.getPassword());
        if (!checkPassword) {
            var failKey = "failed_attemps" + req.username();
            Long failCount = stringRedisTemplate.opsForValue().increment(failKey);
            if(failCount == 1) stringRedisTemplate.expire(failKey, Duration.ofHours(1));
            if (failCount >= 3) {
//                userAuth.setIsUserBlocked(1);
//                userAuthRepository.saveAndFlush(userAuth);
                blockUserNow.blockUserNow(userAuth.getUserId());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akun diblokir karena 3 kali gagal login. Hubungi CS.");
            }
            throw  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username atau Password salah");
        }

        stringRedisTemplate.delete("login_failed:" + req.username());

        var profile = profileRepository.findById(userAuth.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile tidak ditemukan"));
        var emailTo = userAuth.getEmailAddress() != null
                ? userAuth.getEmailAddress()
                : profile.getEmailAddress();

//        var rateKey = "otp_count:" + userAuth.getUserId();
//        Long count = stringRedisTemplate.opsForValue().increment(rateKey);
//        if (count == 1 ) stringRedisTemplate.expire(rateKey, ISSUE_WINDOW);
//        if (count > ISSUE_LIMIT)
//            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Terlalu banyak permintaan OTP. Silahkan coba lagi beberapa menit");

        // generate OTP
        var otp = String.format("%06d", new Random().nextInt(999999));
        var sessionId = UUID.randomUUID().toString();
        var otpKey = "otp:" + userAuth.getUserId();

        Map<String, String> otpData = Map.of(
                "userId", userAuth.getUserId(),
                "sessionId", sessionId
//                "email", userAuth.getEmailAddress(),
//                "otp", otp,
//                "otpCreatedAt", LocalDateTime.now(),
//                "otpExpiresAt", OTP_TTL.toMinutes()
        );

        stringRedisTemplate.opsForHash().putAll(otpKey, otpData);
        stringRedisTemplate.expire(otpKey, OTP_TTL);

        log.info("🧩 Menyimpan OTP ke Redis key={} ttl={}menit", otpKey, OTP_TTL.toMinutes());
        stringRedisTemplate.opsForHash().putAll(otpKey, otpData);
        stringRedisTemplate.expire(otpKey, OTP_TTL);
        log.info("✅ Key {} berhasil disimpan di Redis: {}", otpKey, redisTemplate.hasKey(otpKey));


        // sent OTP by email
        emailService.sendOtp(emailTo, otp);

        return new LoginResponse(true, "Kode OTP telah dikirim ke email Anda", userAuth.getUserId());
    }

    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest req) {
        var otpKey = "otp:" + req.user_id();
        Map<Object, Object> otpData = stringRedisTemplate.opsForHash().entries(otpKey);

        if(otpData.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP exipired atau tidak ditemukan");

        var storedOtp = (String) otpData.get("otp");
        if (!req.otp_code().equals(storedOtp)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "OTP SALAH");

        stringRedisTemplate.delete(otpKey);
        stringRedisTemplate.delete("otp_count:" + req.user_id());

        var role = roleManagementRepository.findFirstByUserId(req.user_id())
                .map(RoleManagement::getRoleName)
                .orElse("Nasabah");
        var userData = userAuthRepository.findById(req.user_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found"));

        var token = jwtUtils.generateToken(req.user_id(), role);

        var sessionKey = "session:" + req.user_id();
        stringRedisTemplate.opsForValue().set(sessionKey, token);
        stringRedisTemplate.expire(sessionKey, TOKEN_TTL);


        var dataUser = new VerifyOtpResponse.User(
                userData.getUserId(),
                userData.getUsername(),
                role
        );

        return new VerifyOtpResponse(true, "login berhasil", token, dataUser);
    }

    @Transactional
    public LogoutResponse logout(String token, String userId){
        var sessionKey = "session:" + userId;
        stringRedisTemplate.delete(sessionKey);

        var blacklistKey = "jwt_blacklist:" + token;
        stringRedisTemplate.opsForValue().set(blacklistKey, "true");

        long ttlSeconds = jwtUtils.getRemainingValidity(token);
        stringRedisTemplate.expire(blacklistKey, Duration.ofSeconds(ttlSeconds));

        return new LogoutResponse(true, "logout berhasil");
    }

}