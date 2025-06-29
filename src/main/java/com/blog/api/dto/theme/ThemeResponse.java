package com.blog.api.dto.theme;

import com.blog.api.entity.Theme;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ThemeResponse {
    private Long id;
    private String name;
    private String description;
    private String slug;
    private String thumbnail;
    private boolean isActive;
    private int postsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ThemeResponse fromTheme(Theme theme) {
        ThemeResponse response = new ThemeResponse();
        response.setId(theme.getId());
        response.setName(theme.getName());
        response.setDescription(theme.getDescription());
        response.setSlug(theme.getSlug());
        response.setThumbnail(theme.getThumbnail());
        response.setActive(theme.isActive());
        response.setPostsCount(theme.getPosts().size());
        response.setCreatedAt(theme.getCreatedAt());
        response.setUpdatedAt(theme.getUpdatedAt());
        return response;
    }
} 