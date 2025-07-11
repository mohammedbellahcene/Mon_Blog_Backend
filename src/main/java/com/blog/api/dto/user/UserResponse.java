package com.blog.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String[] roles;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 