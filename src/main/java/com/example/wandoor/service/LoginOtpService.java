package com.example.wandoor.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.example.wandoor.model.request.*;
import com.example.wandoor.model.response.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.wandoor.model.entity.RoleManagement;
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
public class LoginOtpService {
    private final UserAuthRepository userAuthRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(5);
    private final UserOtpVerificationRepository userOtpVerificationRepository;
    private final EmailService emailService;
    private final Helpers helpers;
    private final ProfileRepository profileRepository;
    private final RoleManagementRepository roleManagementRepository;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate stringRedisTemplate;
    private final BlockUserNow blockUserNow;

    private static final Duration OTP_TTL = Duration.ofMinutes(3);
    private static final Duration COOLDOWN_TTL = Duration.ofSeconds(120);
    private static final Duration BLOCK_TTL = Duration.ofMinutes(10);
    private static final Duration LOGIN_FAIL_TTL = Duration.ofHours(1);
    private static final Duration ISSUE_WINDOW = Duration.ofMinutes(10);
    private static final int ISSUE_LIMIT = 3;
    private static final Duration TOKEN_TTL = Duration.ofHours(2);
    private static final Duration VERIFY_TTL = Duration.ofMinutes(10);

    private static final int MAX_LOGIN_FAIL = 3;
    private static final int MAX_OTP_FAIL = 3;

    @Transactional
    public LoginResponse login(LoginRequest req) {
        var userAuth = userAuthRepository.findByUsername(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username atau Password salah"));

        if (userAuth.getIsUserBlocked() != null && Integer.valueOf(1).equals(userAuth.getIsUserBlocked())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akun diblokir, hubungi CS untuk membuka blokir.");
        }

        var temporaryBlockAccount = "otp:blocked:user:" + req.username();
        if (stringRedisTemplate.hasKey(temporaryBlockAccount)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "akun diblokir sementara, tunggu beberapa saat");
        }

        var checkPassword = passwordEncoder.matches(req.password(), userAuth.getPassword());
        if (!checkPassword) {
            var failKey = "otp:login:failed:" + req.username();
            Long failCount = stringRedisTemplate.opsForValue().increment(failKey);
            if (failCount == 1) stringRedisTemplate.expire(failKey, LOGIN_FAIL_TTL);
            if (failCount >= MAX_LOGIN_FAIL) {
                blockUserNow.blockUserNow(userAuth.getUserId());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akun diblokir karena 3 kali gagal login. Hubungi CS.");
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username atau Password salah");
        }

        stringRedisTemplate.delete("otp:login_failed:" + req.username());

        var sessionId = UUID.randomUUID().toString();
        var otp = String.format("%06d", new Random().nextInt(999999));

        var otpSessionKey = "otp:session:" + sessionId;
        Map<String, String> otpData = Map.of(
                "userId", userAuth.getUserId(),
                "username", userAuth.getUsername(),
                "email", userAuth.getEmailAddress(),
                "otp", otp
        );
        stringRedisTemplate.opsForHash().putAll(otpSessionKey, otpData);
        stringRedisTemplate.expire(otpSessionKey, OTP_TTL);


        var profile = profileRepository.findById(userAuth.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile tidak ditemukan"));
        var emailTo = userAuth.getEmailAddress() != null
                ? userAuth.getEmailAddress()
                : profile.getEmailAddress();

        // sent OTP by email
        emailService.sendOtp(emailTo, otp);

        log.info("OTP {} dikirim ke {} | session={} TTL={}m", otp, userAuth.getEmailAddress(), sessionId, OTP_TTL.toMinutes());

        return new LoginResponse(true, "Kode OTP telah dikirim ke email Anda", sessionId);

    }

    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest req) {
        var otpSessionKey = "otp:session:" + req.sessionId();
        Map<Object, Object> otpData = stringRedisTemplate.opsForHash().entries(otpSessionKey);

        if (otpData.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired atau tidak ditemukan");

        var username = (String) otpData.get("username");
        var storedOtp = (String) otpData.get("otp");
        var userId = (String) otpData.get("userId");

        var verifyAttemptKey = "otp:verify_attempt:" + req.sessionId();
        Long attemptCount = 0L;
        // counter attempt
        if (!req.otpCode().equals(storedOtp)) {
            attemptCount = stringRedisTemplate.opsForValue().increment(verifyAttemptKey);
        if (attemptCount == 1) stringRedisTemplate.expire(verifyAttemptKey, OTP_TTL);
            log.warn("OTP salah (attempt ke {}) untuk user {}", attemptCount, username);

            if (attemptCount >= MAX_OTP_FAIL) {
                stringRedisTemplate.opsForValue().set("otp:blocked:user:" + username, "true", BLOCK_TTL);
                log.warn("User {} diblokir sementara selama {} menit", username, BLOCK_TTL.toMinutes());
                return new VerifyOtpResponse(
                        false,
                        "Terlalu banyak percobaan OTP. Akun diblokir sementara.",
                        null,
                        null,
                        attemptCount.intValue()
                );
            }

            return new VerifyOtpResponse(
                    false,
                    "OTP salah. Percobaan ke-" + attemptCount + " dari " + MAX_OTP_FAIL + ".",
                    null,
                    null,
                    attemptCount.intValue()
            );
        }

        stringRedisTemplate.delete(otpSessionKey);
        stringRedisTemplate.delete("otp:verify_attempt:" + req.sessionId());

        var role = roleManagementRepository.findFirstByUserId(userId)
                .map(RoleManagement::getRoleName)
                .orElse("NASABAH");
        var userData = userAuthRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found"));
        var profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "profile not found"));

        var token = jwtUtils.generateToken(userId, role);

        var sessionKey = "session:" + req.sessionId();
        stringRedisTemplate.opsForValue().set(sessionKey, token, TOKEN_TTL);

        var dataUser = new VerifyOtpResponse.User(
                userData.getUserId(),
                profile.getCif(),
                userData.getUsername(),
                role
        );

        return new VerifyOtpResponse(true, "login berhasil", token, dataUser, attemptCount.intValue());
    }

    @Transactional
    public ResendOtpResponse resendOtp(ResendOtpRequest req) {
        var sessionKey = "otp:session:" + req.sessionId();
        var sessionData = stringRedisTemplate.opsForHash().entries(sessionKey);

        if (sessionData.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session tidak ditemukan atau OTP expired, silahkan login ulang");

        var username = (String) sessionData.get("username");
        var email = (String) sessionData.get("email");

//        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey("otp:blocked:user:" + username))) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akun sedang diblokir sementara karena terlalu banyak percobaan OTP.");
//        }

        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey("otp:cooldown:user:" + username))) {
            long remaining = stringRedisTemplate.getExpire("otp:cooldown:user:" + username, TimeUnit.SECONDS);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Tunggu " + remaining + " detik sebelum mengirim OTP lagi.");
        }

        var resendKey = "otp:resend:user:" + username;
        Long resendCount = stringRedisTemplate.opsForValue().increment(resendKey);
        if (resendCount == 1) stringRedisTemplate.expire(resendKey, Duration.ofMinutes(10));

        log.info("User {} melakukan resend OTP ke-{} dari 3", username, resendCount);

        if (resendCount >= 3) {
            stringRedisTemplate.opsForValue().set("otp:cooldown:user:" + username, "true", Duration.ofSeconds(120));
            stringRedisTemplate.opsForValue().set("otp:blocked:user:" + username, "true", Duration.ofMinutes(10));

            var finalOtp = String.format("%06d", new Random().nextInt(999999));
            stringRedisTemplate.opsForHash().put(sessionKey, "otp", finalOtp);
            stringRedisTemplate.expire(sessionKey, Duration.ofMinutes(3));
            emailService.sendOtp(email, finalOtp);

            log.warn("User {} sudah 3x resend OTP, diblokir sementara & cooldown aktif 120s", username);

            return new ResendOtpResponse(
                    true,
                    "Kode OTP baru telah dikirim. Anda telah mencapai batas resend maksimal. Tunggu 2 menit sebelum mencoba lagi.",
                    120
            );
        }

            var newOtp = String.format("%06d", new Random().nextInt(999999));
            stringRedisTemplate.opsForHash().put(sessionKey, "otp", newOtp);
            stringRedisTemplate.expire(sessionKey, Duration.ofMinutes(3));

            emailService.sendOtp(email, newOtp);

            return new ResendOtpResponse(
                    true,
                    "Kode OTP baru telah dikirim ke email Anda. Resend ke-" + resendCount + " dari 3.",
                    0
            );
    }

    @Transactional
    public LogoutResponse logout(String token, String userId) {
        var sessionKey = "session:" + userId;
        stringRedisTemplate.delete(sessionKey);

        var blacklistKey = "jwt_blacklist:" + token;
        stringRedisTemplate.opsForValue().set(blacklistKey, "true");

        long ttlSeconds = jwtUtils.getRemainingValidity(token);
        stringRedisTemplate.expire(blacklistKey, Duration.ofSeconds(ttlSeconds));

        return new LogoutResponse(true, "logout berhasil");
    }

    public ForgotPasswordResponse requestOtp(ForgotPasswordRequest req){
        var user = userAuthRepository.findByUsername(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        var temporaryBlockKey = "otp:blocked:user:" + user.getUsername();
        if (stringRedisTemplate.hasKey(temporaryBlockKey)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Akun sedang diblokir sementara, silakan coba lagi nanti.");
        }

        var sessionId = UUID.randomUUID().toString();
        var otp = String.format("%06d", new Random().nextInt(999999));

        var keyForgotOtp = "otp:forgot:" + sessionId;
        stringRedisTemplate.opsForHash().putAll(keyForgotOtp, Map.of(
                "username", user.getUsername(),
                "email", user.getEmailAddress(),
                "otp", otp
        ));

        stringRedisTemplate.expire(keyForgotOtp, OTP_TTL);

        emailService.sendOtp(user.getEmailAddress(), otp);
        return new ForgotPasswordResponse(
                true,
                "Kode OTP telah dikirim ke email Anda.",
                sessionId
        );
    }

    public VerifyForgotOtpResponse verifyForgotOtp(VerifyOtpRequest req){
        var otpData = stringRedisTemplate.opsForHash().entries("otp:forgot:" + req.sessionId());
        if (otpData == null || otpData.isEmpty()){
            throw new ResponseStatusException(HttpStatus.GONE, "OTP sudah kadaluarsa atau tidak ditemukan");
        }

        var username = (String) otpData.get("username");
        var otp = (String) otpData.get("otp");

        if (!req.otpCode().equals(otp)) {
        var verifyAttemptKey = "otp:forgot:fail:" + username;
        Long attemptCount = stringRedisTemplate.opsForValue().increment(verifyAttemptKey);
        if (attemptCount == 1) stringRedisTemplate.expire(verifyAttemptKey, OTP_TTL);

            log.warn("OTP salah (attempt ke {}) untuk user {}", attemptCount, username);

            if (attemptCount >= MAX_OTP_FAIL) {
                stringRedisTemplate.opsForValue().set("otp:blocked:user:" + username, "true", BLOCK_TTL);
                log.warn("User {} diblokir sementara selama {} menit", username, BLOCK_TTL.toMinutes());
                return new VerifyForgotOtpResponse(
                        false,
                        "Terlalu banyak percobaan OTP, akun di blokir sementara",
                        null
                );
            }

            return new VerifyForgotOtpResponse(
                    false,
                    "Otp salah. percobaan ke-" + attemptCount + "dari  " + MAX_OTP_FAIL,
                    null
            );

        }
            var verifiedKey = "otp:forgot:verified:" + username;
            stringRedisTemplate.opsForValue().set(verifiedKey, "true", VERIFY_TTL);
            stringRedisTemplate.delete("otp:forgot:" + req.sessionId());
            return new VerifyForgotOtpResponse(true, "Verifikasi OTP berhasil. Silakan buat password baru.", verifiedKey);
    }

    public BaseResponse resetPassword(ResetPasswordRequest req){
        var isVerified = stringRedisTemplate.hasKey(req.verifiedSession());
        if (!isVerified) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session verifikasi tidak valid atau kadaluarsa");

        if(!req.newPassword().equals(req.confirmPassword())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "konfirmasi password tidak cocok");

        if (!req.newPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password tidak memenuhi kriteria");

        var username = req.verifiedSession().replace("otp:forgot:verified:", "");
        var user = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));

        user.setPassword(passwordEncoder.encode(req.newPassword()));
        userAuthRepository.save(user);

        stringRedisTemplate.delete(req.verifiedSession());
        stringRedisTemplate.delete("otp:forgot:fail:" + username);
        return new BaseResponse(true, "Password berhasil diperbarui. Silakan login kembali.");
    }


}




