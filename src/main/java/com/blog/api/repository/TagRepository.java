package com.blog.api.repository;

import com.blog.api.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    
    List<Tag> findByNameIn(List<String> names);
    
    @Query("SELECT t FROM Tag t ORDER BY t.usageCount DESC")
    Page<Tag> findMostUsedTags(Pageable pageable);
    
    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT(:prefix, '%'))")
    List<Tag> findByNameStartingWithIgnoreCase(String prefix);
    
    @Query("SELECT t FROM Tag t WHERE t.usageCount > 0 ORDER BY t.usageCount DESC")
    List<Tag> findActiveTags();
    
    @Query("SELECT COUNT(t) FROM Tag t WHERE t.usageCount > 0")
    long countActiveTags();
} 