package com.blog.api.controller;

import com.blog.api.dto.post.PostCreateRequest;
import com.blog.api.dto.post.PostResponse;
import com.blog.api.entity.User;
import com.blog.api.repository.UserRepository;
import com.blog.api.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Articles", description = "API pour la gestion des articles du blog")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PostController {
    private final PostService postService;
    private final UserRepository userRepository;

    @Operation(summary = "Récupérer tous les articles",
            description = "Récupère la liste paginée de tous les articles publiés")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des articles récupérée avec succès",
                content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @GetMapping("/theme/{themeId}")
    public ResponseEntity<Page<PostResponse>> getPostsByTheme(
            @PathVariable Long themeId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByTheme(themeId, pageable));
    }

    @GetMapping("/author/{username}")
    public ResponseEntity<Page<PostResponse>> getPostsByAuthor(
            @PathVariable String username,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByAuthor(username, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostResponse>> searchPosts(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(postService.searchPosts(keyword, pageable));
    }

    @Operation(summary = "Récupérer un article par son ID",
            description = "Récupère les détails d'un article spécifique")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Article trouvé",
                content = @Content(schema = @Schema(implementation = PostResponse.class))),
        @ApiResponse(responseCode = "404", description = "Article non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(
            @Parameter(description = "ID de l'article", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @Operation(
        summary = "Créer un nouvel article",
        description = """
            Crée un nouvel article avec le contenu fourni.
            
            ## Fonctionnalités
            * Support du format Markdown pour le contenu
            * Génération automatique d'un excerpt
            * Association avec des tags et un thème
            * Planification de publication
            
            ## Notes
            * L'utilisateur doit être authentifié
            * Le titre doit être unique
            * Le contenu doit avoir au moins 100 caractères
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Article créé avec succès",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PostResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "id": 1,
                        "title": "Mon premier article",
                        "content": "Contenu de l'article en Markdown...",
                        "excerpt": "Résumé automatique...",
                        "author": {
                            "id": 1,
                            "username": "john.doe",
                            "email": "john@example.com"
                        },
                        "theme": {
                            "id": 1,
                            "name": "Technologie"
                        },
                        "tags": ["java", "spring"],
                        "status": "DRAFT",
                        "createdAt": "2024-03-16T10:30:00Z",
                        "updatedAt": "2024-03-16T10:30:00Z"
                    }
                    """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostResponse> createPost(
            @Parameter(
                description = "Données de l'article à créer",
                required = true,
                examples = {
                    @ExampleObject(
                        name = "Article simple",
                        value = """
                            {
                                "title": "Mon premier article",
                                "content": "# Introduction\\n\\nContenu de l'article...",
                                "themeId": 1,
                                "tags": ["java", "spring"],
                                "status": "DRAFT"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Article planifié",
                        value = """
                            {
                                "title": "Article planifié",
                                "content": "# Introduction\\n\\nContenu de l'article...",
                                "themeId": 1,
                                "tags": ["java", "spring"],
                                "status": "SCHEDULED",
                                "publishAt": "2024-03-20T14:00:00Z"
                            }
                            """
                    )
                }
            )
            @Valid @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.createPost(request, userDetails.getUsername()));
    }

    @Operation(summary = "Mettre à jour un article",
            description = "Met à jour un article existant. L'utilisateur doit être l'auteur ou un administrateur.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Article mis à jour avec succès",
                content = @Content(schema = @Schema(implementation = PostResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Non autorisé"),
        @ApiResponse(responseCode = "404", description = "Article non trouvé")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostResponse> updatePost(
            @Parameter(description = "ID de l'article", required = true)
            @PathVariable Long id,
            @Parameter(description = "Données de mise à jour", required = true)
            @Valid @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.updatePost(id, request, userDetails.getUsername()));
    }

    @Operation(summary = "Supprimer un article",
            description = "Supprime un article existant. L'utilisateur doit être l'auteur ou un administrateur.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Article supprimé avec succès"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Non autorisé"),
        @ApiResponse(responseCode = "404", description = "Article non trouvé")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "ID de l'article", required = true)
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostResponse>> getAllPostsAsList() {
        return ResponseEntity.ok(postService.getAllPostsAsList());
    }
} 