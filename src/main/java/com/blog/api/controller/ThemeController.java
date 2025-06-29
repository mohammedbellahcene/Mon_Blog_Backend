package com.blog.api.controller;

import com.blog.api.dto.theme.ThemeRequest;
import com.blog.api.dto.theme.ThemeResponse;
import com.blog.api.service.ThemeService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Thèmes", description = "API pour la gestion des thèmes du blog")
@RestController
@RequestMapping("/themes")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    @Operation(summary = "Créer un nouveau thème",
            description = "Crée un nouveau thème pour les articles. Nécessite des droits d'administrateur.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Thème créé avec succès",
                content = @Content(schema = @Schema(implementation = ThemeResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ThemeResponse> createTheme(
            @Parameter(description = "Données du thème", required = true)
            @Valid @RequestBody ThemeRequest request) {
        return ResponseEntity.ok(themeService.createTheme(request));
    }

    @Operation(summary = "Récupérer tous les thèmes",
            description = "Récupère la liste paginée de tous les thèmes disponibles")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des thèmes récupérée avec succès",
                content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getAllThemes() {
        return ResponseEntity.ok(themeService.getAllThemes());
    }

    @Operation(summary = "Récupérer un thème par son ID",
            description = "Récupère les détails d'un thème spécifique")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thème trouvé",
                content = @Content(schema = @Schema(implementation = ThemeResponse.class))),
        @ApiResponse(responseCode = "404", description = "Thème non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ThemeResponse> getThemeById(
            @Parameter(description = "ID du thème", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(themeService.getThemeById(id));
    }

    @Operation(summary = "Mettre à jour un thème",
            description = "Modifie un thème existant. Nécessite des droits d'administrateur.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thème mis à jour avec succès",
                content = @Content(schema = @Schema(implementation = ThemeResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Non autorisé"),
        @ApiResponse(responseCode = "404", description = "Thème non trouvé")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ThemeResponse> updateTheme(
            @Parameter(description = "ID du thème", required = true)
            @PathVariable Long id,
            @Parameter(description = "Données de mise à jour", required = true)
            @Valid @RequestBody ThemeRequest request) {
        return ResponseEntity.ok(themeService.updateTheme(id, request));
    }

    @Operation(summary = "Supprimer un thème",
            description = "Supprime un thème existant. Nécessite des droits d'administrateur.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Thème supprimé avec succès"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Non autorisé"),
        @ApiResponse(responseCode = "404", description = "Thème non trouvé")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTheme(
            @Parameter(description = "ID du thème", required = true)
            @PathVariable Long id) {
        themeService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }
} 