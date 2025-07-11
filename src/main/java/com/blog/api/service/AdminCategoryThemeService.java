package com.blog.api.service;

import com.blog.api.entity.Category;
import com.blog.api.entity.Theme;
import com.blog.api.repository.CategoryRepository;
import com.blog.api.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminCategoryThemeService {
    private final CategoryRepository categoryRepository;
    private final ThemeRepository themeRepository;

    // Catégories
    public Iterable<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category updatedCategory) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(updatedCategory.getName());
        category.setSlug(updatedCategory.getSlug());
        category.setDescription(updatedCategory.getDescription());
        category.setDisplayOrder(updatedCategory.getDisplayOrder());
        category.setParent(updatedCategory.getParent());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    // Thèmes
    public Iterable<Theme> getAllThemes() {
        return themeRepository.findAll();
    }

    public Optional<Theme> getThemeById(Long id) {
        return themeRepository.findById(id);
    }

    @Transactional
    public Theme createTheme(Theme theme) {
        return themeRepository.save(theme);
    }

    @Transactional
    public Theme updateTheme(Long id, Theme updatedTheme) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theme not found"));
        theme.setName(updatedTheme.getName());
        theme.setSlug(updatedTheme.getSlug());
        theme.setDescription(updatedTheme.getDescription());
        theme.setThumbnail(updatedTheme.getThumbnail());
        theme.setActive(updatedTheme.isActive());
        return themeRepository.save(theme);
    }

    @Transactional
    public void deleteTheme(Long id) {
        if (!themeRepository.existsById(id)) {
            throw new RuntimeException("Theme not found");
        }
        themeRepository.deleteById(id);
    }
} 