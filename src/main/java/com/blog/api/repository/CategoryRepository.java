package com.blog.api.repository;

import com.blog.api.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
    
    List<Category> findByParentIsNull();
    
    List<Category> findByParentOrderByDisplayOrder(Category parent);
    
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.displayOrder")
    Page<Category> findRootCategories(Pageable pageable);
    
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Category> searchCategories(String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Post p WHERE p.category = :category")
    long countPostsByCategory(Category category);
    
    boolean existsBySlug(String slug);
    
    @Query("SELECT c FROM Category c WHERE c.postCount > 0 ORDER BY c.postCount DESC")
    List<Category> findPopularCategories(Pageable pageable);
} 