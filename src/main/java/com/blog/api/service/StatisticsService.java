package com.blog.api.service;

import com.blog.api.entity.Post;
import com.blog.api.entity.User;
import com.blog.api.repository.CommentRepository;
import com.blog.api.repository.PostRepository;
import com.blog.api.repository.ReactionRepository;
import com.blog.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;

    public Map<String, Object> getGlobalStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalPosts", postRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalComments", commentRepository.count());
        
        // Statistiques des 30 derniers jours
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        stats.put("newPostsLast30Days", postRepository.countByCreatedAtAfter(thirtyDaysAgo));
        stats.put("newUsersLast30Days", userRepository.countByCreatedAtAfter(thirtyDaysAgo));
        
        return stats;
    }

    public Map<String, Object> getUserStatistics(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalPosts", postRepository.countByAuthor(user));
        stats.put("totalComments", commentRepository.countByAuthor(user));
        
        // Engagement sur les posts de l'utilisateur
        long totalLikes = 0;
        long totalDislikes = 0;
        for (Post post : postRepository.findByAuthor(user)) {
            totalLikes += reactionRepository.countLikesByPost(post);
            totalDislikes += reactionRepository.countDislikesByPost(post);
        }
        stats.put("totalLikes", totalLikes);
        stats.put("totalDislikes", totalDislikes);
        
        return stats;
    }

    public Map<String, Object> getPostStatistics(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Map<String, Object> stats = new HashMap<>();
        
        stats.put("viewCount", post.getViewCount());
        stats.put("commentCount", commentRepository.countByPost(post));
        stats.put("likes", reactionRepository.countLikesByPost(post));
        stats.put("dislikes", reactionRepository.countDislikesByPost(post));
        
        return stats;
    }

    public void incrementViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }
} 