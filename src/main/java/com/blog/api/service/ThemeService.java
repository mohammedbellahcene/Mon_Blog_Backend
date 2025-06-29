package com.blog.api.service;

import com.blog.api.dto.theme.ThemeRequest;
import com.blog.api.dto.theme.ThemeResponse;
import com.blog.api.entity.Theme;
import com.blog.api.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThemeService {
    private final ThemeRepository themeRepository;

    public List<ThemeResponse> getAllThemes() {
        return themeRepository.findAll().stream()
                .map(ThemeResponse::fromTheme)
                .collect(Collectors.toList());
    }

    public ThemeResponse getThemeById(Long id) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theme not found"));

        return ThemeResponse.fromTheme(theme);
    }

    @Transactional
    public ThemeResponse createTheme(ThemeRequest request) {
        if (themeRepository.existsBySlug(request.getSlug())) {
            throw new RuntimeException("Theme slug already exists");
        }

        Theme theme = new Theme();
        theme.setName(request.getName());
        theme.setDescription(request.getDescription());
        theme.setSlug(request.getSlug());
        theme.setThumbnail(request.getThumbnail());
        theme.setActive(request.isActive());

        Theme savedTheme = themeRepository.save(theme);
        return ThemeResponse.fromTheme(savedTheme);
    }

    @Transactional
    public ThemeResponse updateTheme(Long id, ThemeRequest request) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theme not found"));

        if (!theme.getSlug().equals(request.getSlug()) && themeRepository.existsBySlug(request.getSlug())) {
            throw new RuntimeException("Theme slug already exists");
        }

        theme.setName(request.getName());
        theme.setDescription(request.getDescription());
        theme.setSlug(request.getSlug());
        theme.setThumbnail(request.getThumbnail());
        theme.setActive(request.isActive());

        Theme updatedTheme = themeRepository.save(theme);
        return ThemeResponse.fromTheme(updatedTheme);
    }

    @Transactional
    public void deleteTheme(Long id) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theme not found"));

        if (!theme.getPosts().isEmpty()) {
            throw new RuntimeException("Cannot delete theme with associated posts");
        }

        themeRepository.delete(theme);
    }
} 