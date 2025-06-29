package com.blog.api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;

    @Email(message = "L'email doit être valide")
    private String email;

    @Size(max = 1000, message = "La bio ne doit pas dépasser 1000 caractères")
    private String bio;

    private String avatar;

    private String currentPassword;

    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String newPassword;
} 