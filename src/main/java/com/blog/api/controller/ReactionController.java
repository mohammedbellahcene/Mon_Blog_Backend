package com.blog.api.controller;

import com.blog.api.dto.reaction.ReactionRequest;
import com.blog.api.dto.reaction.ReactionResponse;
import com.blog.api.dto.reaction.ReactionStatsResponse;
import com.blog.api.service.ReactionService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Réactions", description = "API pour la gestion des réactions (likes/dislikes) sur les articles")
@RestController
@RequestMapping("/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @Operation(summary = "Ajouter une réaction",
            description = "Ajoute une réaction (like/dislike) à un article. L'utilisateur doit être authentifié.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Réaction ajoutée avec succès",
                content = @Content(schema = @Schema(implementation = ReactionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "404", description = "Article non trouvé"),
        @ApiResponse(responseCode = "409", description = "L'utilisateur a déjà réagi à cet article")
    })
    @PostMapping("/posts/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReactionResponse> addReaction(
            @PathVariable Long postId,
            @Parameter(description = "Données de la réaction", required = true)
            @Valid @RequestBody ReactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reactionService.addReaction(postId, request, userDetails.getUsername()));
    }

    @Operation(summary = "Supprimer une réaction",
            description = "Supprime une réaction existante. L'utilisateur doit être l'auteur de la réaction.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Réaction supprimée avec succès"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Non autorisé"),
        @ApiResponse(responseCode = "404", description = "Réaction non trouvée")
    })
    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteReaction(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        reactionService.deleteReaction(postId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtenir les statistiques de réactions d'un article",
            description = "Récupère le nombre de likes et dislikes pour un article spécifique")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès",
                content = @Content(schema = @Schema(implementation = ReactionStatsResponse.class))),
        @ApiResponse(responseCode = "404", description = "Article non trouvé")
    })
    @GetMapping("/posts/{postId}/stats")
    public ResponseEntity<ReactionStatsResponse> getReactionStats(
            @PathVariable Long postId) {
        return ResponseEntity.ok(reactionService.getReactionStats(postId));
    }

    @Operation(summary = "Vérifier la réaction d'un utilisateur",
            description = "Vérifie si l'utilisateur connecté a déjà réagi à un article spécifique")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vérification effectuée avec succès",
                content = @Content(schema = @Schema(implementation = ReactionResponse.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "404", description = "Article non trouvé")
    })
    @GetMapping("/posts/{postId}/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReactionResponse> getUserReaction(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reactionService.getUserReaction(postId, userDetails.getUsername()));
    }
} 