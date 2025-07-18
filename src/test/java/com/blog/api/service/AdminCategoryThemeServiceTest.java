package com.blog.api.service;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;
import java.util.*;
import com.blog.api.entity.*;
import com.blog.api.repository.*;

class AdminCategoryThemeServiceTest {
    @Mock private CategoryRepository categoryRepository;
    @Mock private ThemeRepository themeRepository;
    @InjectMocks private AdminCategoryThemeService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    // Catégories
    @Test
    void getAllCategories_success() {
        Category cat = new Category();
        when(categoryRepository.findAll()).thenReturn(Collections.singletonList(cat));
        Iterable<Category> result = service.getAllCategories();
        assertTrue(result.iterator().hasNext());
    }

    @Test
    void getCategoryById_success() {
        Category cat = new Category();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        assertTrue(service.getCategoryById(1L).isPresent());
    }

    @Test
    void getCategoryById_notFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertFalse(service.getCategoryById(1L).isPresent());
    }

    @Test
    void createCategory_success() {
        Category cat = new Category();
        when(categoryRepository.save(cat)).thenReturn(cat);
        assertEquals(cat, service.createCategory(cat));
    }

    @Test
    void updateCategory_notFound_throwsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.updateCategory(1L, new Category()));
    }

    @Test
    void updateCategory_success() {
        Category cat = new Category();
        Category updated = new Category();
        updated.setName("new");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(categoryRepository.save(any())).thenReturn(cat);
        Category result = service.updateCategory(1L, updated);
        assertEquals("new", result.getName());
    }

    @Test
    void deleteCategory_notFound_throwsException() {
        when(categoryRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.deleteCategory(1L));
    }

    @Test
    void deleteCategory_success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);
        assertDoesNotThrow(() -> service.deleteCategory(1L));
    }

    // Thèmes
    @Test
    void getAllThemes_success() {
        Theme theme = new Theme();
        when(themeRepository.findAll()).thenReturn(Collections.singletonList(theme));
        Iterable<Theme> result = service.getAllThemes();
        assertTrue(result.iterator().hasNext());
    }

    @Test
    void getThemeById_success() {
        Theme theme = new Theme();
        when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
        assertTrue(service.getThemeById(1L).isPresent());
    }

    @Test
    void getThemeById_notFound() {
        when(themeRepository.findById(1L)).thenReturn(Optional.empty());
        assertFalse(service.getThemeById(1L).isPresent());
    }

    @Test
    void createTheme_success() {
        Theme theme = new Theme();
        when(themeRepository.save(theme)).thenReturn(theme);
        assertEquals(theme, service.createTheme(theme));
    }

    @Test
    void updateTheme_notFound_throwsException() {
        when(themeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.updateTheme(1L, new Theme()));
    }

    @Test
    void updateTheme_success() {
        Theme theme = new Theme();
        Theme updated = new Theme();
        updated.setName("new");
        when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
        when(themeRepository.save(any())).thenReturn(theme);
        Theme result = service.updateTheme(1L, updated);
        assertEquals("new", result.getName());
    }

    @Test
    void deleteTheme_notFound_throwsException() {
        when(themeRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.deleteTheme(1L));
    }

    @Test
    void deleteTheme_success() {
        when(themeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(themeRepository).deleteById(1L);
        assertDoesNotThrow(() -> service.deleteTheme(1L));
    }
} 