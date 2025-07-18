package com.blog.api.service;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;
import java.util.*;
import org.springframework.data.domain.PageImpl;
import java.time.LocalDateTime;
import com.blog.api.entity.*;
import com.blog.api.repository.*;

class StatisticsServiceTest {
    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private ReactionRepository reactionRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private StatisticsService statisticsService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getGlobalStatistics_success() {
        when(postRepository.count()).thenReturn(10L);
        when(userRepository.count()).thenReturn(5L);
        when(commentRepository.count()).thenReturn(20L);
        when(postRepository.countByCreatedAtAfter(any())).thenReturn(2L);
        when(userRepository.countByCreatedAtAfter(any())).thenReturn(1L);
        Map<String, Object> stats = statisticsService.getGlobalStatistics();
        assertEquals(10L, stats.get("totalPosts"));
        assertEquals(5L, stats.get("totalUsers"));
        assertEquals(20L, stats.get("totalComments"));
        assertEquals(2L, stats.get("newPostsLast30Days"));
        assertEquals(1L, stats.get("newUsersLast30Days"));
    }

    @Test
    void getUserStatistics_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> statisticsService.getUserStatistics("user"));
    }

    @Test
    void getUserStatistics_success() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(postRepository.countByAuthor(user)).thenReturn(3L);
        when(commentRepository.countByAuthor(user)).thenReturn(4L);
        Post post = new Post();
        List<Post> posts = Collections.singletonList(post);
        when(postRepository.findByAuthor(user)).thenReturn(posts);
        when(reactionRepository.countLikesByPost(post)).thenReturn(2L);
        when(reactionRepository.countDislikesByPost(post)).thenReturn(1L);
        Map<String, Object> stats = statisticsService.getUserStatistics("user");
        assertEquals(3L, stats.get("totalPosts"));
        assertEquals(4L, stats.get("totalComments"));
        assertEquals(2L, stats.get("totalLikes"));
        assertEquals(1L, stats.get("totalDislikes"));
    }

    @Test
    void getPostStatistics_postNotFound_throwsException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> statisticsService.getPostStatistics(1L));
    }

    @Test
    void getPostStatistics_success() {
        Post post = new Post();
        post.setViewCount(5);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.countByPost(post)).thenReturn(3L);
        when(reactionRepository.countLikesByPost(post)).thenReturn(2L);
        when(reactionRepository.countDislikesByPost(post)).thenReturn(1L);
        Map<String, Object> stats = statisticsService.getPostStatistics(1L);
        assertEquals(5, stats.get("viewCount"));
        assertEquals(3L, stats.get("commentCount"));
        assertEquals(2L, stats.get("likes"));
        assertEquals(1L, stats.get("dislikes"));
    }

    @Test
    void incrementViewCount_postNotFound_throwsException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> statisticsService.incrementViewCount(1L));
    }

    @Test
    void incrementViewCount_success() {
        Post post = new Post();
        post.setViewCount(1);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        statisticsService.incrementViewCount(1L);
        assertEquals(2, post.getViewCount());
    }
} 