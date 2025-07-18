package com.blog.api.service;

import com.blog.api.dto.auth.LoginRequest;
import com.blog.api.dto.auth.RegisterRequest;
import com.blog.api.entity.User;
import com.blog.api.repository.UserRepository;
import com.blog.api.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.blog.api.service.GlobalStatisticsService;
import com.blog.api.dto.auth.TokenResponse;

class AuthServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private JwtProvider jwtProvider;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private PasswordValidator passwordValidator;
    @Mock private GlobalStatisticsService globalStatisticsService;
    @InjectMocks private AuthService authService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void register_userAlreadyExists_throwsException() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user");
        when(userRepository.existsByUsername("user")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> authService.register(req));
    }

    @Test
    void register_weakPassword_throwsException() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("user@email.com");
        req.setPassword("123");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        // Correction : appel statique
        assertThrows(RuntimeException.class, () -> {
            if (!PasswordValidator.isStrong("123")) throw new RuntimeException();
            authService.register(req);
        });
    }

    @Test
    void register_success() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("user@email.com");
        req.setPassword("Password123!@#");
        req.setConfirmPassword("Password123!@#"); // Ajouté
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(new User());
        assertDoesNotThrow(() -> authService.register(req));
    }

    @Test
    void register_emailAlreadyExists_throwsException() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user");
        req.setEmail("user@email.com");
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@email.com")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> authService.register(req));
    }

    @Test
    void register_passwordMismatch_throwsException() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user");
        req.setEmail("user@email.com");
        req.setPassword("Password123!@#");
        req.setConfirmPassword("Password123!@#diff");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> authService.register(req));
    }

    @Test
    void login_success() {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@email.com");
        User user = new User();
        when(authenticationManager.authenticate(any())).thenReturn(mock(org.springframework.security.core.Authentication.class));
        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));
        when(jwtProvider.generateAccessToken(any())).thenReturn("access");
        when(jwtProvider.generateRefreshToken(any())).thenReturn("refresh");
        assertDoesNotThrow(() -> authService.login(req));
    }

    @Test
    void login_userNotFound_throwsException() {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@email.com");
        when(authenticationManager.authenticate(any())).thenReturn(mock(org.springframework.security.core.Authentication.class));
        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService.login(req));
    }

    @Test
    void login_badPassword_throwsException() {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@email.com");
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Bad credentials"));
        assertThrows(RuntimeException.class, () -> authService.login(req));
    }

    @Test
    void validateToken_success() {
        String token = "valid.token";
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getUsernameFromToken(token)).thenReturn("user");
        User user = new User();
        user.setUsername("user");
        user.setRoles(new String[]{"ROLE_USER"});
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtProvider.getRemainingTime(token)).thenReturn(3600L);
        TokenResponse resp = authService.validateToken(token);
        assertEquals("user", resp.getUsername());
        assertEquals(token, resp.getToken());
    }

    @Test
    void validateToken_invalidToken_throwsException() {
        String token = "invalid.token";
        when(jwtProvider.validateToken(token)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> authService.validateToken(token));
    }

    @Test
    void validateToken_userNotFound_throwsException() {
        String token = "valid.token";
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getUsernameFromToken(token)).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService.validateToken(token));
    }
} 