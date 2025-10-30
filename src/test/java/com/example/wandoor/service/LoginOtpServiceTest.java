package com.example.wandoor.service;

import com.example.wandoor.model.entity.OtpVerification;
import com.example.wandoor.model.entity.Profile;
import com.example.wandoor.model.entity.RoleManagement;
import com.example.wandoor.model.entity.UserAuth;
import com.example.wandoor.model.request.LoginRequest;
import com.example.wandoor.model.request.VerifyOtpRequest;
import com.example.wandoor.repository.*;
import com.example.wandoor.service.EmailService;
import com.example.wandoor.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LoginOtpServiceTest {

    @Mock
    private UserAuthRepository userAuthRepository;
    @Mock
    private UserOtpVerificationRepository userOtpVerificationRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private RoleManagementRepository roleManagementRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private HashOperations<String, String, String> hashOperations;
    @Mock
    private ValueOperations<String, String> valueOperations;



    @InjectMocks
    private LoginOtpService loginOtpService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(stringRedisTemplate.opsForHash()).thenReturn((HashOperations) hashOperations);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.hasKey(anyString())).thenReturn(false);
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(stringRedisTemplate.expire(anyString(), any())).thenReturn(true);
        when(stringRedisTemplate.delete(anyString())).thenReturn(true);
        doNothing().when(hashOperations).putAll(anyString(), anyMap());

    }

    @AfterEach
    void cleanRedis() {
        stringRedisTemplate.delete("otp:*");
    }


    @Test
    void testLogin_success() {
        var req = new LoginRequest("oktaviaqa", "123456");

        var userAuth = new UserAuth();
        userAuth.setUserId("U001");
        userAuth.setUsername("oktaviaqa");
        userAuth.setPassword(encoder.encode("123456"));
        userAuth.setEmailAddress("oktaviaqa@example.com");
        userAuth.setIsUserBlocked(0);

        when(userAuthRepository.findByUsername("oktaviaqa")).thenReturn(Optional.of(userAuth));

        var profile = new Profile();
        profile.setId("U001");
        profile.setEmailAddress("oktaviaqa@example.com");
        when(profileRepository.findById("U001")).thenReturn(Optional.of(profile));

        when(stringRedisTemplate.hasKey(anyString())).thenReturn(false);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForHash()).thenReturn((HashOperations) hashOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(stringRedisTemplate.expire(anyString(), any())).thenReturn(true);
        when(stringRedisTemplate.delete(anyString())).thenReturn(true);

        doNothing().when(hashOperations).putAll(anyString(), anyMap());

        doNothing().when(emailService).sendOtp(anyString(), anyString());

//        when(userOtpVerificationRepository.save(any(OtpVerification.class)))
//                .thenAnswer(inv -> {
//                    OtpVerification otp = inv.getArgument(0);
//                    otp.setId(UUID.randomUUID().toString());
//                    return otp;
//                });

        var response = loginOtpService.login(req);

        assertThat(response.status()).isTrue();
        assertThat(response.message()).contains("Kode OTP");
        assertThat(response.sessionId()).isNotNull();

        verify(emailService, times(1)).sendOtp(eq("oktaviaqa@example.com"), anyString());
    }

    @Test
    void testLogin_wrongPassword_shouldThrow() {
        var req = new LoginRequest("oktaviaqa", "wrongpass");

        var userAuth = new UserAuth();
        userAuth.setUserId("U001");
        userAuth.setUsername("oktaviaqa");
        userAuth.setPassword(encoder.encode("123456"));
        userAuth.setIsUserBlocked(0);

        when(userAuthRepository.findByUsername("oktaviaqa")).thenReturn(Optional.of(userAuth));

        when(stringRedisTemplate.hasKey(anyString())).thenReturn(false);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(stringRedisTemplate.expire(anyString(), any())).thenReturn(true);

        var ex = catchThrowable(() -> loginOtpService.login(req));

        assertThat(ex)
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Username atau Password salah");
    }

    @Test
    void testLogin_blockedUser_shouldThrow() {
        var req = new LoginRequest("oktaviaqa", "123456");

        var userAuth = new UserAuth();
        userAuth.setUserId("U001");
        userAuth.setUsername("oktaviaqa");
        userAuth.setPassword(encoder.encode("123456"));
        userAuth.setIsUserBlocked(1);

        when(userAuthRepository.findByUsername("oktaviaqa")).thenReturn(Optional.of(userAuth));

        var ex = catchThrowable(() -> loginOtpService.login(req));

        assertThat(ex)
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Akun diblokir");
    }

    @Test
    void testVerifyOtp_success() {
        var otpId = UUID.randomUUID().toString();
        var req = new VerifyOtpRequest(otpId.toString(), "654321");

        var otpVerification = OtpVerification.builder()
                .id(otpId)
                .otpCode("654321")
                .userId("U001")
                .isUsed(0)
                .expiresAt(LocalDateTime.now().plusMinutes(2))
                .build();
        var profile = new Profile();
        profile.setId("U001");
        profile.setEmailAddress("oktaviaqa@example.com");

        String otpSessionKey = "otp:session:" + otpId;
        Map<Object, Object> mockOtpData = new HashMap<>();
        mockOtpData.put("otp", "654321");
        mockOtpData.put("username", "oktaviaqa");
        mockOtpData.put("userId", "U001");


        when(userOtpVerificationRepository.consumeIfValid(anyString(), anyString(), any()))
                .thenReturn(1);
        when(userOtpVerificationRepository.findById(anyString())).thenReturn(Optional.of(otpVerification));
        when(userAuthRepository.findById(eq("U001"))).thenReturn(Optional.of(new UserAuth("U001", "oktaviaqa", null, null, null, 0)));
        when(profileRepository.findById("U001")).thenReturn(Optional.of(profile));
        when(jwtUtils.generateToken(anyString(), anyString())).thenReturn("jwt_token");

        when(stringRedisTemplate.opsForHash()).thenReturn((HashOperations) hashOperations);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(hashOperations.entries(eq(otpSessionKey))).thenReturn((Map) mockOtpData);
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(stringRedisTemplate.expire(anyString(), any())).thenReturn(true);

        var response = loginOtpService.verifyOtp(req);

        assertThat(response.status()).isTrue();
        assertThat(response.message()).isEqualTo("login berhasil");
        assertThat(response.token()).isEqualTo("jwt_token");
        assertThat(response.user().username()).isEqualTo("oktaviaqa");
    }

    @Test
    void testVerifyOtp_expired_shouldThrow() {
        var otpId = UUID.randomUUID().toString();
        var req = new VerifyOtpRequest(otpId.toString(), "123456");

        String otpSessionKey = "otp:session:" + otpId;

        when(stringRedisTemplate.opsForHash()).thenReturn((HashOperations) hashOperations);
        when(hashOperations.entries(eq(otpSessionKey))).thenReturn(Map.of());

        when(userAuthRepository.findById(eq("U001"))).thenReturn(Optional.of(new UserAuth("U001", "oktaviaqa", null, null, null, 0)));

        var ex = catchThrowable(() -> loginOtpService.verifyOtp(req));

        assertThat(ex)
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("OTP expired");
    }
}