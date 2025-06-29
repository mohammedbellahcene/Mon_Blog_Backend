package com.blog.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private String username;
    private String[] roles;
} 