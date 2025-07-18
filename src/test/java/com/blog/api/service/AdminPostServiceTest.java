package com.blog.api.service;

import com.blog.api.entity.Post;
import com.blog.api.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.util.Optional;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminPostServiceTest {
    @Mock private PostRepository postRepository;
    @InjectMocks private AdminPostService adminPostService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getAllPosts_success() {
        Post post = new Post();
        when(postRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(post)));
        Page<Post> result = adminPostService.getAllPosts(PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getPostById_notFound_returnsEmpty() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertTrue(adminPostService.getPostById(1L).isEmpty());
    }

    @Test
    void updatePost_postNotFound_throwsException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        Post updated = new Post();
        assertThrows(RuntimeException.class, () -> adminPostService.updatePost(1L, updated));
    }

    @Test
    void updatePost_success() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("old");
        Post updated = new Post();
        updated.setTitle("new");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any())).thenReturn(post);
        Post result = adminPostService.updatePost(1L, updated);
        assertEquals("new", result.getTitle());
    }

    @Test
    void deletePost_postNotFound_throwsException() {
        when(postRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> adminPostService.deletePost(1L));
    }

    @Test
    void deletePost_success() {
        when(postRepository.existsById(1L)).thenReturn(true);
        doNothing().when(postRepository).deleteById(1L);
        assertDoesNotThrow(() -> adminPostService.deletePost(1L));
        verify(postRepository, times(1)).deleteById(1L);
    }

    @Test
    void setFeatured_postNotFound_throwsException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> adminPostService.setFeatured(1L, true));
    }

    @Test
    void setFeatured_success() {
        Post post = new Post();
        post.setId(1L);
        post.setFeatured(false);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any())).thenReturn(post);
        Post result = adminPostService.setFeatured(1L, true);
        assertTrue(result.isFeatured());
    }
} 