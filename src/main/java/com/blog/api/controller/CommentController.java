package com.blog.api.controller;

import com.blog.api.dto.comment.CommentRequest;
import com.blog.api.dto.comment.CommentResponse;
import com.blog.api.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Commentaires", description = "API pour la gestion des commentaires")
@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "Créer un nouveau commentaire",
            description = "Ajoute un commentaire à un article. L'utilisateur doit être authentifié.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Commentaire créé avec succès",
                content = @Content(schema = @Schema(implementation = CommentResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "404", description = "Article non trouvé")
    })
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Parameter(description = "ID de l'article", required = true)
            @PathVariable Long postId,
            @Parameter(description = "Données du commentaire", required = true)
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(commentService.createComment(postId, request, userDetails.getUsername()));
    }

    @Operation(summary = "Récupérer les commentaires d'un article",
            description = "Récupère la liste paginée des commentaires d'un article spécifique")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des commentaires récupérée avec succès",
                content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "404", description = "Article non trouvé")
    })
    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getCommentsByPost(
            @Parameter(description = "ID de l'article", required = true)
            @PathVariable Long postId,
            @Parameter(description = "Paramètres de pagination")
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, pageable));
    }

    @Operation(summary = "Mettre à jour un commentaire",
            description = "Modifie un commentaire existant. L'utilisateur doit être l'auteur du commentaire.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Commentaire mis à jour avec succès",
                content = @Content(schema = @Schema(implementation = CommentResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Non autorisé"),
        @ApiResponse(responseCode = "404", description = "Commentaire non trouvé")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @Parameter(description = "ID du commentaire", required = true)
            @PathVariable Long commentId,
            @Parameter(description = "Données de mise à jour", required = true)
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request, userDetails.getUsername()));
    }

    @Operation(summary = "Supprimer un commentaire",
            description = "Supprime un commentaire existant. L'utilisateur doit être l'auteur du commentaire ou un administrateur.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Commentaire supprimé avec succès"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Non autorisé"),
        @ApiResponse(responseCode = "404", description = "Commentaire non trouvé")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID du commentaire", required = true)
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
} 