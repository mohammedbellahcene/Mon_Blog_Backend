package com.blog.api.controller;

import com.blog.api.dto.auth.AuthResponse;
import com.blog.api.dto.auth.LoginRequest;
import com.blog.api.dto.auth.RegisterRequest;
import com.blog.api.dto.auth.TokenRequest;
import com.blog.api.dto.auth.TokenResponse;
import com.blog.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentification", description = "API pour l'authentification et l'inscription des utilisateurs")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Inscription d'un nouvel utilisateur",
            description = "Permet à un nouvel utilisateur de créer un compte")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inscription réussie",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données d'inscription invalides"),
        @ApiResponse(responseCode = "409", description = "Nom d'utilisateur ou email déjà utilisé")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Parameter(description = "Données d'inscription", required = true)
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Connexion utilisateur",
            description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Connexion réussie",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données de connexion invalides"),
        @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Données de connexion", required = true)
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Vérification de token",
            description = "Vérifie si un token JWT est valide")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token valide"),
        @ApiResponse(responseCode = "401", description = "Token invalide ou expiré")
    })
    @PostMapping("/verify")
    public ResponseEntity<TokenResponse> verifyToken(@RequestBody TokenRequest request) {
        return ResponseEntity.ok(authService.validateToken(request.getToken()));
    }
} 