package com.blog.api.repository;

import com.blog.api.entity.Theme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    Optional<Theme> findBySlug(String slug);
    
    List<Theme> findByIsActiveTrue();
    
    @Query("SELECT t FROM Theme t WHERE t.isActive = true ORDER BY t.name")
    Page<Theme> findActiveThemes(Pageable pageable);
    
    @Query("SELECT t FROM Theme t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Theme> searchThemes(String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Post p WHERE p.theme = :theme")
    long countPostsByTheme(Theme theme);
    
    boolean existsBySlug(String slug);
} 