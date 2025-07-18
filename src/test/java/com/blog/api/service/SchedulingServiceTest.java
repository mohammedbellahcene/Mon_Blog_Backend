package com.blog.api.service;

import com.blog.api.entity.Post;
import com.blog.api.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SchedulingServiceTest {
    @Mock PostRepository postRepository;
    @InjectMocks SchedulingService schedulingService;
    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void publishScheduledPosts_postsToPublish_success() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Titre");
        post.setStatus(Post.Status.SCHEDULED);
        post.setPublishedAt(LocalDateTime.now().minusMinutes(5));
        when(postRepository.findByStatusAndPublishedAtBefore(eq(Post.Status.SCHEDULED), any())).thenReturn(List.of(post));
        when(postRepository.save(any())).thenReturn(post);
        schedulingService.publishScheduledPosts();
        assertEquals(Post.Status.PUBLISHED, post.getStatus());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void publishScheduledPosts_noPosts_nothingHappens() {
        when(postRepository.findByStatusAndPublishedAtBefore(eq(Post.Status.SCHEDULED), any())).thenReturn(Collections.emptyList());
        schedulingService.publishScheduledPosts();
        verify(postRepository, never()).save(any());
    }
} 