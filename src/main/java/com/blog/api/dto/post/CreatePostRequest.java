package com.blog.api.dto.post;

import com.blog.api.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(
    name = "CreatePostRequest",
    description = "DTO pour la création d'un nouvel article",
    example = """
        {
            "title": "Introduction à Spring Boot",
            "content": "# Spring Boot\\n\\nSpring Boot est un framework...",
            "excerpt": "Découvrez Spring Boot et ses fonctionnalités",
            "themeId": 1,
            "tags": ["spring", "java", "tutorial"],
            "status": "DRAFT",
            "publishAt": "2024-03-20T14:00:00Z"
        }
        """
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {

    @Schema(
        description = "Titre de l'article",
        example = "Introduction à Spring Boot",
        minLength = 3,
        maxLength = 100,
        required = true
    )
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 100, message = "Le titre doit contenir entre 3 et 100 caractères")
    private String title;

    @Schema(
        description = "Contenu de l'article en format Markdown",
        example = "# Introduction\n\nCeci est le contenu de l'article...",
        minLength = 100,
        required = true
    )
    @NotBlank(message = "Le contenu est obligatoire")
    @Size(min = 100, message = "Le contenu doit contenir au moins 100 caractères")
    private String content;

    @Schema(
        description = "Résumé de l'article (optionnel, généré automatiquement si non fourni)",
        example = "Découvrez Spring Boot et ses fonctionnalités",
        maxLength = 200
    )
    @Size(max = 200, message = "L'excerpt ne doit pas dépasser 200 caractères")
    private String excerpt;

    @Schema(
        description = "ID du thème de l'article",
        example = "1",
        required = true
    )
    @NotNull(message = "Le thème est obligatoire")
    private Long themeId;

    @Schema(
        description = "Liste des tags associés à l'article",
        example = "[\"spring\", \"java\", \"tutorial\"]"
    )
    private Set<String> tags;

    @Schema(
        description = "Statut de l'article",
        example = "DRAFT",
        allowableValues = {"DRAFT", "PUBLISHED", "SCHEDULED", "ARCHIVED"},
        defaultValue = "DRAFT",
        required = true
    )
    @NotNull(message = "Le statut est obligatoire")
    private Post.Status status;

    @Schema(
        description = "Date de publication planifiée (requise si status = SCHEDULED)",
        example = "2024-03-20T14:00:00Z",
        type = "string",
        format = "date-time"
    )
    @Future(message = "La date de publication doit être dans le futur")
    private LocalDateTime publishAt;
} 