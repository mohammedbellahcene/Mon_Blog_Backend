package com.blog.api.service;

import com.blog.api.dto.reaction.ReactionRequest;
import com.blog.api.entity.Post;
import com.blog.api.entity.Reaction;
import com.blog.api.entity.User;
import com.blog.api.repository.PostRepository;
import com.blog.api.repository.ReactionRepository;
import com.blog.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReactionServiceTest {
    @Mock private ReactionRepository reactionRepository;
    @Mock private UserRepository userRepository;
    @Mock private PostRepository postRepository;
    @Mock private GlobalStatisticsService globalStatisticsService;
    @InjectMocks private ReactionService reactionService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void addReaction_userNotFound_throwsException() {
        ReactionRequest req = new ReactionRequest();
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reactionService.addReaction(1L, req, "user"));
    }

    @Test
    void addReaction_postNotFound_throwsException() {
        ReactionRequest req = new ReactionRequest();
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reactionService.addReaction(1L, req, "user"));
    }

    @Test
    void addReaction_like_success() {
        ReactionRequest req = new ReactionRequest();
        req.setType(Reaction.ReactionType.LIKE);
        User user = new User();
        Post post = new Post();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(reactionRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());
        Reaction reaction = new Reaction();
        reaction.setUser(user);
        reaction.setPost(post); // Ajouté
        when(reactionRepository.save(any())).thenReturn(reaction);
        assertDoesNotThrow(() -> reactionService.addReaction(1L, req, "user"));
    }

    @Test
    void deleteReaction_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reactionService.deleteReaction(1L, "user"));
    }

    @Test
    void deleteReaction_postNotFound_throwsException() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reactionService.deleteReaction(1L, "user"));
    }

    @Test
    void deleteReaction_success_like() {
        User user = new User();
        Post post = new Post();
        Reaction reaction = new Reaction();
        reaction.setType(Reaction.ReactionType.LIKE);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(reactionRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(reaction));
        doNothing().when(globalStatisticsService).decrementLikes();
        doNothing().when(reactionRepository).deleteByUserAndPost(user, post);
        assertDoesNotThrow(() -> reactionService.deleteReaction(1L, "user"));
    }

    @Test
    void getReactionStats_postNotFound_throwsException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reactionService.getReactionStats(1L));
    }

    @Test
    void getReactionStats_success() {
        Post post = new Post();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(reactionRepository.countByPostAndType(post, Reaction.ReactionType.LIKE)).thenReturn(2L);
        when(reactionRepository.countByPostAndType(post, Reaction.ReactionType.DISLIKE)).thenReturn(1L);
        assertDoesNotThrow(() -> reactionService.getReactionStats(1L));
    }

    @Test
    void getUserReaction_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reactionService.getUserReaction(1L, "user"));
    }

    @Test
    void getUserReaction_postNotFound_throwsException() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reactionService.getUserReaction(1L, "user"));
    }

    @Test
    void getUserReaction_noReaction_returnsNull() {
        User user = new User();
        Post post = new Post();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(reactionRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());
        assertNull(reactionService.getUserReaction(1L, "user"));
    }

    @Test
    void getUserReaction_success() {
        User user = new User();
        Post post = new Post();
        Reaction reaction = new Reaction();
        reaction.setUser(user);
        reaction.setPost(post);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(reactionRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(reaction));
        assertNotNull(reactionService.getUserReaction(1L, "user"));
    }

    @Test
    void addReaction_dislike_success() {
        ReactionRequest req = new ReactionRequest();
        req.setType(Reaction.ReactionType.DISLIKE);
        User user = new User();
        Post post = new Post();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(reactionRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());
        Reaction reaction = new Reaction();
        reaction.setUser(user);
        reaction.setPost(post);
        when(reactionRepository.save(any())).thenReturn(reaction);
        doNothing().when(globalStatisticsService).incrementDislikes();
        assertDoesNotThrow(() -> reactionService.addReaction(1L, req, "user"));
        verify(globalStatisticsService, times(1)).incrementDislikes();
    }

    @Test
    void addReaction_existingReaction_success() {
        ReactionRequest req = new ReactionRequest();
        req.setType(Reaction.ReactionType.LIKE);
        User user = new User();
        Post post = new Post();
        Reaction existing = new Reaction();
        existing.setUser(user);
        existing.setPost(post);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(reactionRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(existing));
        when(reactionRepository.save(any())).thenReturn(existing);
        doNothing().when(globalStatisticsService).incrementLikes();
        assertDoesNotThrow(() -> reactionService.addReaction(1L, req, "user"));
        verify(globalStatisticsService, times(1)).incrementLikes();
    }

    @Test
    void deleteReaction_success_dislike() {
        User user = new User();
        Post post = new Post();
        Reaction reaction = new Reaction();
        reaction.setType(Reaction.ReactionType.DISLIKE);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(reactionRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(reaction));
        doNothing().when(globalStatisticsService).decrementDislikes();
        doNothing().when(reactionRepository).deleteByUserAndPost(user, post);
        assertDoesNotThrow(() -> reactionService.deleteReaction(1L, "user"));
        verify(globalStatisticsService, times(1)).decrementDislikes();
    }

    @Test
    void deleteReaction_noReaction_success() {
        User user = new User();
        Post post = new Post();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(reactionRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());
        doNothing().when(reactionRepository).deleteByUserAndPost(user, post);
        assertDoesNotThrow(() -> reactionService.deleteReaction(1L, "user"));
        verify(globalStatisticsService, never()).decrementLikes();
        verify(globalStatisticsService, never()).decrementDislikes();
    }

    @Test
    void getReactionStats_zeroRatio() {
        Post post = new Post();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(reactionRepository.countByPostAndType(post, Reaction.ReactionType.LIKE)).thenReturn(0L);
        when(reactionRepository.countByPostAndType(post, Reaction.ReactionType.DISLIKE)).thenReturn(0L);
        assertDoesNotThrow(() -> {
            var stats = reactionService.getReactionStats(1L);
            assertEquals(0.0, stats.getLikeRatio());
        });
    }
} 