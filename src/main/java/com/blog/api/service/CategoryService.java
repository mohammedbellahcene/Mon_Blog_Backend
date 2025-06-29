package com.blog.api.service;

import com.blog.api.dto.category.CategoryRequest;
import com.blog.api.dto.category.CategoryResponse;
import com.blog.api.entity.Category;
import com.blog.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByParentIsNull()
                .stream()
                .map(CategoryResponse::fromCategory)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getTopLevelCategories() {
        return categoryRepository.findRootCategories(PageRequest.of(0, 10))
                .stream()
                .map(CategoryResponse::fromCategory)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return CategoryResponse.fromCategory(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        validateCategoryRequest(request);

        Category category = new Category();
        updateCategoryFromRequest(category, request);

        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.fromCategory(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        validateCategoryRequest(request);
        validateCategoryHierarchy(category, request.getParentId());

        updateCategoryFromRequest(category, request);

        Category updatedCategory = categoryRepository.save(category);
        return CategoryResponse.fromCategory(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getPosts().isEmpty()) {
            throw new RuntimeException("Cannot delete category with associated posts");
        }

        categoryRepository.delete(category);
    }

    private void validateCategoryRequest(CategoryRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Category name is required");
        }

        String slug = request.getSlug();
        if (slug != null && !slug.trim().isEmpty() && categoryRepository.existsBySlug(slug)) {
            throw new RuntimeException("Category slug already exists");
        }
    }

    private void validateCategoryHierarchy(Category category, Long newParentId) {
        if (newParentId != null) {
            Category newParent = categoryRepository.findById(newParentId)
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));

            if (category.getId().equals(newParentId)) {
                throw new RuntimeException("Category cannot be its own parent");
            }

            if (newParent.isDescendantOf(category)) {
                throw new RuntimeException("Circular hierarchy detected");
            }
        }
    }

    private void updateCategoryFromRequest(Category category, CategoryRequest request) {
        category.setName(request.getName());
        
        if (request.getSlug() != null && !request.getSlug().trim().isEmpty()) {
            category.setSlug(request.getSlug());
        }
        
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }
    }
} 