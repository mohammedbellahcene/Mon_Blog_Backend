package com.blog.api.service;

import com.blog.api.repository.CommentRepository;
import com.blog.api.repository.PostRepository;
import com.blog.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public Map<String, Object> getGlobalStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalPosts", postRepository.count());
        stats.put("totalComments", commentRepository.count());
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        stats.put("newUsersLast30Days", userRepository.countByCreatedAtAfter(thirtyDaysAgo));
        stats.put("newPostsLast30Days", postRepository.countByCreatedAtAfter(thirtyDaysAgo));
        return stats;
    }
} 