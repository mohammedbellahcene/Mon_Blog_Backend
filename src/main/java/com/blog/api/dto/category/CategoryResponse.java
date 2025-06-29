package com.blog.api.dto.category;

import com.blog.api.entity.Category;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private String color;
    private Integer displayOrder;
    private Integer postCount;
    private Long parentId;
    private List<CategoryResponse> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CategoryResponse fromCategory(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setSlug(category.getSlug());
        response.setDescription(category.getDescription());
        response.setIcon(category.getIcon());
        response.setColor(category.getColor());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setPostCount(category.getPostCount());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        
        if (category.getParent() != null) {
            response.setParentId(category.getParent().getId());
        }

        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            response.setChildren(category.getChildren().stream()
                .map(CategoryResponse::fromCategory)
                .collect(Collectors.toList()));
        }

        return response;
    }
} 