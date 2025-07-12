package com.blog.api.repository;

import com.blog.api.entity.Post;
import com.blog.api.entity.Tag;
import com.blog.api.entity.Theme;
import com.blog.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    Page<Post> findAllOrderByCreatedAtDesc(Pageable pageable);
    
    Page<Post> findByTheme(Theme theme, Pageable pageable);
    Page<Post> findByAuthor(User author, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Page<Post> searchByKeyword(String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Post p WHERE p.author = :author")
    long countByAuthor(User author);

    List<Post> findByStatusAndPublishedAtBefore(Post.Status status, LocalDateTime publishedAt);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' ORDER BY p.publishedAt DESC")
    Page<Post> findPublishedPosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.author = :author AND p.status = :status")
    Page<Post> findByAuthorAndStatus(User author, Post.Status status, Pageable pageable);

    @Query("""
        SELECT DISTINCT p FROM Post p
        LEFT JOIN p.tags t
        LEFT JOIN p.theme th
        WHERE (:keyword IS NULL OR (
            LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ))
        AND (:themeIds IS NULL OR th.id IN :themeIds)
        AND (:startDate IS NULL OR p.publishedAt >= :startDate)
        AND (:endDate IS NULL OR p.publishedAt <= :endDate)
        AND (:tags IS NULL OR t.name IN :tags)
        AND (:authorUsername IS NULL OR p.author.username = :authorUsername)
        AND (:status IS NULL OR p.status = :status)
        ORDER BY p.createdAt DESC
        """)
    Page<Post> findBySearchCriteria(
        @Param("keyword") String keyword,
        @Param("themeIds") List<Long> themeIds,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("tags") List<String> tags,
        @Param("authorUsername") String authorUsername,
        @Param("status") Post.Status status,
        Pageable pageable
    );

    @Query("SELECT DISTINCT t.name FROM Post p JOIN p.tags t WHERE t.name LIKE CONCAT(:prefix, '%')")
    List<String> findTagsByPrefix(@Param("prefix") String prefix);

    @Query("""
        SELECT DISTINCT p FROM Post p
        LEFT JOIN p.tags t
        WHERE p.id != :postId
        AND (t.name IN :tags OR p.theme.id = :themeId)
        AND p.status = 'PUBLISHED'
        GROUP BY p
        ORDER BY 
        CASE 
            WHEN COUNT(CASE WHEN t.name IN :tags THEN 1 END) > 0 AND p.theme.id = :themeId THEN 1
            WHEN COUNT(CASE WHEN t.name IN :tags THEN 1 END) > 0 THEN 2
            WHEN p.theme.id = :themeId THEN 3
            ELSE 4
        END,
        p.createdAt DESC
        """)
    Page<Post> findSimilarPosts(
        @Param("postId") Long postId,
        @Param("tags") List<String> tags,
        @Param("themeId") Long themeId,
        Pageable pageable);

    @Query("""
        SELECT t, COUNT(p)
        FROM Post p JOIN p.tags t
        WHERE p.status = 'PUBLISHED'
        GROUP BY t
        ORDER BY COUNT(p) DESC
        LIMIT :limit
        """)
    List<String> findMostUsedTags(int limit);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.createdAt >= :date")
    long countByCreatedAtAfter(LocalDateTime date);

    List<Post> findByAuthor(User author);

    @Query("""
        SELECT p FROM Post p
        WHERE (:query IS NULL OR 
              LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR 
              LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')))
        AND (:author IS NULL OR p.author = :author)
        AND (:tag IS NULL OR :tag IN (SELECT t.name FROM p.tags t))
        AND (:dateFrom IS NULL OR p.createdAt >= :dateFrom)
        AND (:dateTo IS NULL OR p.createdAt <= :dateTo)
        AND p.status = 'PUBLISHED'
        """)
    Page<Post> findBySearchCriteria(
        @Param("query") String query,
        @Param("author") User author,
        @Param("tag") String tag,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo,
        Pageable pageable);

    @Query("""
        SELECT p FROM Post p
        JOIN p.tags t
        WHERE p.id != :postId
        AND t IN :tags
        GROUP BY p
        ORDER BY COUNT(t) DESC
        """)
    List<Post> findSimilarPosts(
        @Param("postId") Long postId,
        @Param("tags") List<Tag> tags,
        Pageable pageable);

    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findTop5ByOrderByViewCountDesc();
    @Query(value = "SELECT p.* FROM posts p LEFT JOIN comments c ON c.post_id = p.id GROUP BY p.id ORDER BY COUNT(c.id) DESC LIMIT 5", nativeQuery = true)
    List<Post> findTop5MostCommented();
    List<Post> findTop5ByOrderByLikeCountDesc();
    List<Post> findTop5ByOrderByDislikeCountDesc();
} 