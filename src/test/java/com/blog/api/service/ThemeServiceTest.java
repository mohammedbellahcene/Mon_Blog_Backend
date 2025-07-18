package com.blog.api.service;

import com.blog.api.dto.theme.ThemeRequest;
import com.blog.api.dto.theme.ThemeResponse;
import com.blog.api.entity.Theme;
import com.blog.api.repository.ThemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;

class ThemeServiceTest {
    @Mock private ThemeRepository themeRepository;
    @InjectMocks private ThemeService themeService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getAllThemes_success() {
        Theme theme = new Theme();
        when(themeRepository.findAll()).thenReturn(Collections.singletonList(theme));
        List<ThemeResponse> result = themeService.getAllThemes();
        assertEquals(1, result.size());
    }

    @Test
    void getThemeById_notFound_throwsException() {
        when(themeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> themeService.getThemeById(1L));
    }

    @Test
    void getThemeById_success() {
        Theme theme = new Theme();
        theme.setId(1L);
        when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
        ThemeResponse resp = themeService.getThemeById(1L);
        assertNotNull(resp);
    }

    @Test
    void createTheme_slugExists_throwsException() {
        ThemeRequest req = new ThemeRequest();
        req.setSlug("slug");
        when(themeRepository.existsBySlug("slug")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> themeService.createTheme(req));
    }

    @Test
    void createTheme_success() {
        ThemeRequest req = new ThemeRequest();
        req.setSlug("slug");
        when(themeRepository.existsBySlug("slug")).thenReturn(false);
        Theme theme = new Theme();
        when(themeRepository.save(any())).thenReturn(theme);
        ThemeResponse resp = themeService.createTheme(req);
        assertNotNull(resp);
    }

    @Test
    void updateTheme_notFound_throwsException() {
        ThemeRequest req = new ThemeRequest();
        when(themeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> themeService.updateTheme(1L, req));
    }

    @Test
    void updateTheme_slugExists_throwsException() {
        Theme theme = new Theme();
        theme.setSlug("old");
        ThemeRequest req = new ThemeRequest();
        req.setSlug("new");
        when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
        when(themeRepository.existsBySlug("new")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> themeService.updateTheme(1L, req));
    }

    @Test
    void updateTheme_success() {
        Theme theme = new Theme();
        theme.setSlug("old");
        ThemeRequest req = new ThemeRequest();
        req.setSlug("old");
        when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
        when(themeRepository.save(any())).thenReturn(theme);
        ThemeResponse resp = themeService.updateTheme(1L, req);
        assertNotNull(resp);
    }

    @Test
    void deleteTheme_notFound_throwsException() {
        when(themeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> themeService.deleteTheme(1L));
    }

    @Test
    void deleteTheme_withPosts_throwsException() {
        Theme theme = new Theme();
        java.util.Set<com.blog.api.entity.Post> posts = java.util.Collections.singleton(new com.blog.api.entity.Post());
        theme.setPosts(posts);
        when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
        assertThrows(RuntimeException.class, () -> themeService.deleteTheme(1L));
    }

    @Test
    void deleteTheme_success() {
        Theme theme = new Theme();
        theme.setPosts(java.util.Collections.emptySet());
        when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
        doNothing().when(themeRepository).delete(theme);
        assertDoesNotThrow(() -> themeService.deleteTheme(1L));
    }
} 