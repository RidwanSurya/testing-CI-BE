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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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

    @InjectMocks
    private LoginOtpService loginOtpService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        when(userOtpVerificationRepository.save(any(OtpVerification.class)))
                .thenAnswer(inv -> {
                    OtpVerification otp = inv.getArgument(0);
                    otp.setId(UUID.randomUUID().toString());
                    return otp;
                });

        var response = loginOtpService.login(req);

        assertThat(response.status()).isTrue();
        assertThat(response.message()).contains("Kode OTP");
        assertThat(response.otpRef()).isNotNull();

        verify(emailService, times(1)).sendOtp(eq("oktaviaqa@example.com"), anyString());
    }

    @Test
    void testLogin_wrongPassword_shouldThrow() {
        var req = new LoginRequest("oktaviaqa", "wrongpass");

        var userAuth = new UserAuth();
        userAuth.setUserId("U001");
        userAuth.setUsername("oktaviaqa");
        userAuth.setPassword(encoder.encode("123456"));

        when(userAuthRepository.findByUsername("oktaviaqa")).thenReturn(Optional.of(userAuth));

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

        when(userOtpVerificationRepository.consumeIfValid(anyString(), anyString(), any()))
                .thenReturn(1);
        when(userOtpVerificationRepository.findById(anyString())).thenReturn(Optional.of(otpVerification));
        when(userAuthRepository.findById("U001")).thenReturn(Optional.of(new UserAuth("U001", "oktaviaqa", null, null, null, 0)));
        when(jwtUtils.generateToken(anyString(), anyString())).thenReturn("jwt_token");

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

        var otpVerification = OtpVerification.builder()
                .id(otpId)
                .otpCode("123456")
                .isUsed(0)
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();

        when(userOtpVerificationRepository.consumeIfValid(anyString(), anyString(), any()))
                .thenReturn(0);
        when(userOtpVerificationRepository.findByIdAndIsUsed(anyString(), eq(0)))
                .thenReturn(Optional.of(otpVerification));

        var ex = catchThrowable(() -> loginOtpService.verifyOtp(req));

        assertThat(ex)
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("OTP expired");
    }
}
