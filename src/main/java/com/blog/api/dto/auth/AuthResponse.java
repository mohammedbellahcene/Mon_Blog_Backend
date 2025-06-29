package com.blog.api.dto.auth;

import com.blog.api.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private UserDto user;

    @Data
    @AllArgsConstructor
    public static class UserDto {
        private Long id;
        private String username;
        private String email;
        private String[] roles;

        public static UserDto fromUser(User user) {
            return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
            );
        }
    }
} 