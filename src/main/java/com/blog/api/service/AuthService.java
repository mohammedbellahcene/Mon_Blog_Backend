package com.blog.api.service;

import com.blog.api.dto.auth.AuthResponse;
import com.blog.api.dto.auth.LoginRequest;
import com.blog.api.dto.auth.RegisterRequest;
import com.blog.api.dto.auth.TokenResponse;
import com.blog.api.entity.User;
import com.blog.api.repository.UserRepository;
import com.blog.api.security.JwtProvider;
import com.blog.api.security.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.blog.api.service.PasswordValidator;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Validation de la robustesse du mot de passe
        if (!PasswordValidator.isStrong(request.getPassword())) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 12 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial.");
        }
        // Vérification de la confirmation du mot de passe
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Les mots de passe ne correspondent pas.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(new String[]{UserRole.ROLE_USER});

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        return new AuthResponse(accessToken, refreshToken, "Bearer", AuthResponse.UserDto.fromUser(user));
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        return new AuthResponse(accessToken, refreshToken, "Bearer", AuthResponse.UserDto.fromUser(user));
    }

    public TokenResponse validateToken(String token) {
        if (!jwtProvider.validateToken(token)) {
            throw new RuntimeException("Token invalide ou expiré");
        }

        String username = jwtProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return TokenResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .expiresIn(jwtProvider.getRemainingTime(token))
            .username(user.getUsername())
            .roles(user.getRoles())
            .build();
    }
} 