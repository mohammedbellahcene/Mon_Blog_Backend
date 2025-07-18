package com.blog.api.service;

import com.blog.api.dto.category.CategoryRequest;
import com.blog.api.dto.category.CategoryResponse;
import com.blog.api.entity.Category;
import com.blog.api.repository.CategoryRepository;
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

class CategoryServiceTest {
    @Mock private CategoryRepository categoryRepository;
    @InjectMocks private CategoryService categoryService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getAllCategories_success() {
        Category cat = new Category();
        when(categoryRepository.findByParentIsNull()).thenReturn(Collections.singletonList(cat));
        List<CategoryResponse> result = categoryService.getAllCategories();
        assertEquals(1, result.size());
    }

    @Test
    void getCategoryBySlug_notFound_throwsException() {
        when(categoryRepository.findBySlug("slug")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> categoryService.getCategoryBySlug("slug"));
    }

    @Test
    void createCategory_success() {
        CategoryRequest req = new CategoryRequest();
        req.setName("cat");
        req.setSlug("slug");
        when(categoryRepository.save(any())).thenReturn(new Category());
        assertDoesNotThrow(() -> categoryService.createCategory(req));
    }

    @Test
    void getTopLevelCategories_success() {
        Category cat = new Category();
        org.springframework.data.domain.Page<com.blog.api.entity.Category> page = new org.springframework.data.domain.PageImpl<>(Collections.singletonList(cat));
        when(categoryRepository.findRootCategories(any())).thenReturn(page);
        List<CategoryResponse> result = categoryService.getTopLevelCategories();
        assertEquals(1, result.size());
    }

    @Test
    void getCategoryBySlug_success() {
        Category cat = new Category();
        when(categoryRepository.findBySlug("slug")).thenReturn(Optional.of(cat));
        CategoryResponse resp = categoryService.getCategoryBySlug("slug");
        assertNotNull(resp);
    }

    @Test
    void updateCategory_notFound_throwsException() {
        CategoryRequest req = new CategoryRequest();
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> categoryService.updateCategory(1L, req));
    }

    @Test
    void updateCategory_parentNotFound_throwsException() {
        Category cat = new Category();
        CategoryRequest req = new CategoryRequest();
        req.setParentId(2L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> categoryService.updateCategory(1L, req));
    }

    @Test
    void updateCategory_circularHierarchy_throwsException() {
        Category cat = new Category();
        cat.setId(1L);
        CategoryRequest req = new CategoryRequest();
        req.setParentId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        assertThrows(RuntimeException.class, () -> categoryService.updateCategory(1L, req));
    }

    @Test
    void updateCategory_slugExists_throwsException() {
        Category cat = new Category();
        cat.setId(1L);
        CategoryRequest req = new CategoryRequest();
        req.setName("cat");
        req.setSlug("slug");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(categoryRepository.existsBySlug("slug")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> categoryService.updateCategory(1L, req));
    }

    @Test
    void updateCategory_success() {
        Category cat = new Category();
        cat.setId(1L);
        CategoryRequest req = new CategoryRequest();
        req.setName("cat");
        req.setSlug("slug");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(categoryRepository.save(any())).thenReturn(cat);
        when(categoryRepository.existsBySlug(any())).thenReturn(false);
        CategoryResponse resp = categoryService.updateCategory(1L, req);
        assertNotNull(resp);
    }

    @Test
    void deleteCategory_notFound_throwsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> categoryService.deleteCategory(1L));
    }

    @Test
    void deleteCategory_withPosts_throwsException() {
        Category cat = new Category();
        java.util.List<com.blog.api.entity.Post> posts = java.util.Collections.singletonList(new com.blog.api.entity.Post());
        cat.setPosts(posts);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        assertThrows(RuntimeException.class, () -> categoryService.deleteCategory(1L));
    }

    @Test
    void deleteCategory_success() {
        Category cat = new Category();
        cat.setPosts(new java.util.ArrayList<>());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        doNothing().when(categoryRepository).delete(cat);
        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));
    }

    @Test
    void createCategory_nameEmpty_throwsException() {
        CategoryRequest req = new CategoryRequest();
        req.setName("");
        assertThrows(RuntimeException.class, () -> categoryService.createCategory(req));
    }

    @Test
    void createCategory_slugExists_throwsException() {
        CategoryRequest req = new CategoryRequest();
        req.setName("cat");
        req.setSlug("slug");
        when(categoryRepository.existsBySlug("slug")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> categoryService.createCategory(req));
    }

    @Test
    void updateCategory_withValidParent_success() {
        Category cat = new Category();
        cat.setId(1L);
        Category parent = new Category();
        parent.setId(2L);
        CategoryRequest req = new CategoryRequest();
        req.setName("cat");
        req.setSlug("slug");
        req.setParentId(2L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(any())).thenReturn(cat);
        when(categoryRepository.existsBySlug(any())).thenReturn(false);
        // isDescendantOf = false
        Category spyParent = spy(parent);
        doReturn(false).when(spyParent).isDescendantOf(any());
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(spyParent));
        CategoryResponse resp = categoryService.updateCategory(1L, req);
        assertNotNull(resp);
    }

    @Test
    void updateCategory_withCircularParent_throwsException() {
        Category cat = new Category();
        cat.setId(1L);
        Category parent = spy(new Category());
        parent.setId(2L);
        CategoryRequest req = new CategoryRequest();
        req.setName("cat");
        req.setSlug("slug");
        req.setParentId(2L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(parent));
        doReturn(true).when(parent).isDescendantOf(cat);
        when(categoryRepository.existsBySlug(any())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> categoryService.updateCategory(1L, req));
    }

    @Test
    void createCategory_withNullSlug_success() {
        CategoryRequest req = new CategoryRequest();
        req.setName("cat");
        req.setSlug(null);
        when(categoryRepository.save(any())).thenReturn(new Category());
        assertDoesNotThrow(() -> categoryService.createCategory(req));
    }

    @Test
    void createCategory_withEmptySlug_success() {
        CategoryRequest req = new CategoryRequest();
        req.setName("cat");
        req.setSlug("");
        when(categoryRepository.save(any())).thenReturn(new Category());
        assertDoesNotThrow(() -> categoryService.createCategory(req));
    }

    @Test
    void updateCategory_withNullSlug_success() {
        Category cat = new Category();
        cat.setId(1L);
        CategoryRequest req = new CategoryRequest();
        req.setName("cat");
        req.setSlug(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(categoryRepository.save(any())).thenReturn(cat);
        when(categoryRepository.existsBySlug(any())).thenReturn(false);
        CategoryResponse resp = categoryService.updateCategory(1L, req);
        assertNotNull(resp);
    }

    @Test
    void createCategory_withValidParent_success() {
        CategoryRequest req = new CategoryRequest();
        req.setName("cat");
        req.setSlug("slug");
        req.setParentId(2L);
        Category parent = new Category();
        parent.setId(2L);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(any())).thenReturn(new Category());
        assertDoesNotThrow(() -> categoryService.createCategory(req));
    }
} 