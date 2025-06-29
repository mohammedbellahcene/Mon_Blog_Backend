package com.blog.api.repository;

import com.blog.api.entity.Post;
import com.blog.api.entity.Reaction;
import com.blog.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserAndPost(User user, Post post);
    
    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.post = :post AND r.type = 'LIKE'")
    long countLikesByPost(Post post);
    
    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.post = :post AND r.type = 'DISLIKE'")
    long countDislikesByPost(Post post);
    
    void deleteByUserAndPost(User user, Post post);
    
    long countByPostAndType(Post post, Reaction.ReactionType type);
} 