package com.blog.api.controller;

import com.blog.api.entity.Category;
import com.blog.api.entity.Theme;
import com.blog.api.service.AdminCategoryThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryThemeController {
    private final AdminCategoryThemeService adminCategoryThemeService;

    // Catégories
    @GetMapping("/categories")
    public ResponseEntity<Iterable<Category>> getAllCategories() {
        return ResponseEntity.ok(adminCategoryThemeService.getAllCategories());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return adminCategoryThemeService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(adminCategoryThemeService.createCategory(category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category updatedCategory) {
        return ResponseEntity.ok(adminCategoryThemeService.updateCategory(id, updatedCategory));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        adminCategoryThemeService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // Thèmes
    @GetMapping("/themes")
    public ResponseEntity<Iterable<Theme>> getAllThemes() {
        return ResponseEntity.ok(adminCategoryThemeService.getAllThemes());
    }

    @GetMapping("/themes/{id}")
    public ResponseEntity<Theme> getThemeById(@PathVariable Long id) {
        return adminCategoryThemeService.getThemeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/themes")
    public ResponseEntity<Theme> createTheme(@RequestBody Theme theme) {
        return ResponseEntity.ok(adminCategoryThemeService.createTheme(theme));
    }

    @PutMapping("/themes/{id}")
    public ResponseEntity<Theme> updateTheme(@PathVariable Long id, @RequestBody Theme updatedTheme) {
        return ResponseEntity.ok(adminCategoryThemeService.updateTheme(id, updatedTheme));
    }

    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable Long id) {
        adminCategoryThemeService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }
} 