package com.blog.api.dto.theme;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ThemeCreateRequest {
    @NotBlank(message = "Le nom est requis")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String name;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    private String description;

    @Size(max = 100, message = "Le slug ne doit pas dépasser 100 caractères")
    private String slug;

    private String thumbnail;

    private boolean isActive = true;
} 