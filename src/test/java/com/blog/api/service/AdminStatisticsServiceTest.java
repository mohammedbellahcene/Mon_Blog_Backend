package com.blog.api.service;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;
import java.util.Map;
import java.time.LocalDateTime;

class AdminStatisticsServiceTest {
    @Mock private com.blog.api.repository.UserRepository userRepository;
    @Mock private com.blog.api.repository.PostRepository postRepository;
    @Mock private com.blog.api.repository.CommentRepository commentRepository;
    @InjectMocks private AdminStatisticsService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void contextLoads() {
        // TODO: Ajouter des tests unitaires pour AdminStatisticsService
    }

    @Test
    void getGlobalStatistics_success() {
        when(userRepository.count()).thenReturn(10L);
        when(postRepository.count()).thenReturn(20L);
        when(commentRepository.count()).thenReturn(30L);
        when(userRepository.countByCreatedAtAfter(any(LocalDateTime.class))).thenReturn(2L);
        when(postRepository.countByCreatedAtAfter(any(LocalDateTime.class))).thenReturn(3L);
        Map<String, Object> stats = service.getGlobalStatistics();
        assertEquals(10L, stats.get("totalUsers"));
        assertEquals(20L, stats.get("totalPosts"));
        assertEquals(30L, stats.get("totalComments"));
        assertEquals(2L, stats.get("newUsersLast30Days"));
        assertEquals(3L, stats.get("newPostsLast30Days"));
    }
} 