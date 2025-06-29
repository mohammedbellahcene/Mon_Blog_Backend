package com.blog.api.repository;

import com.blog.api.entity.Comment;
import com.blog.api.entity.Post;
import com.blog.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPost(Post post, Pageable pageable);
    Page<Comment> findByAuthor(User author, Pageable pageable);
    Page<Comment> findByStatus(Comment.ModerationStatus status, Pageable pageable);
    Page<Comment> findByReportCountGreaterThanOrderByReportCountDesc(Integer reportCount, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.author = :author")
    long countByAuthor(User author);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post = :post")
    long countByPost(Post post);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post = :post AND c.status = 'APPROVED'")
    long countApprovedCommentsByPost(Post post);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.author = :author AND c.status = 'APPROVED'")
    long countApprovedCommentsByAuthor(User author);
    
    Page<Comment> findByParent(Comment parent, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.parent IS NULL AND c.status = 'APPROVED'")
    Page<Comment> findTopLevelCommentsByPost(Post post, Pageable pageable);
} 