package com.blog.api.dto.post;

import com.blog.api.entity.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostCreateRequest {
    @NotBlank(message = "Le titre est requis")
    @Size(max = 200, message = "Le titre ne doit pas dépasser 200 caractères")
    private String title;

    @NotBlank(message = "Le contenu est requis")
    private String content;

    @Size(max = 500, message = "L'extrait ne doit pas dépasser 500 caractères")
    private String excerpt;

    @Size(max = 160, message = "La meta description ne doit pas dépasser 160 caractères")
    private String metaDescription;

    @Size(max = 100, message = "Le slug ne doit pas dépasser 100 caractères")
    private String slug;

    private String featuredImage;

    @Size(max = 200, message = "Le texte alternatif ne doit pas dépasser 200 caractères")
    private String featuredImageAlt;

    @Size(max = 500, message = "La légende ne doit pas dépasser 500 caractères")
    private String featuredImageCaption;

    private String ogImage;

    private List<String> galleryImages;

    private Long themeId;

    private List<String> tags;

    private Post.Status status;

    private LocalDateTime publishAt;
} 