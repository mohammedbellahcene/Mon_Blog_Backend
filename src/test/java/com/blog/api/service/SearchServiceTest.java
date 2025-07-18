package com.blog.api.service;

import com.blog.api.repository.PostRepository;
import com.blog.api.repository.ThemeRepository;
import com.blog.api.repository.ReactionRepository;
import com.blog.api.repository.UserRepository;
import com.blog.api.repository.TagRepository;
import com.blog.api.dto.post.PostResponse;
import com.blog.api.entity.Post;
import com.blog.api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchServiceTest {
    @Mock private PostRepository postRepository;
    @Mock private ThemeRepository themeRepository;
    @Mock private ReactionRepository reactionRepository;
    @Mock private UserRepository userRepository;
    @Mock private TagRepository tagRepository;
    @InjectMocks private SearchService searchService;
    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void searchPosts_withResults() {
        Pageable pageable = mock(Pageable.class);
        Post post = new Post();
        User author = new User();
        author.setId(1L);
        post.setAuthor(author);
        Page<Post> page = new PageImpl<>(Collections.singletonList(post));
        when(postRepository.findBySearchCriteria(any(), any(), any(), any(), any(), any(), any(), eq(pageable))).thenReturn(page);
        when(reactionRepository.countLikesByPost(any())).thenReturn(1L);
        when(reactionRepository.countDislikesByPost(any())).thenReturn(0L);
        Page<PostResponse> result = searchService.searchPosts("test", null, null, null, null, null, null, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchPosts_noResults() {
        Pageable pageable = mock(Pageable.class);
        when(postRepository.findBySearchCriteria(any(), any(), any(), any(), any(), any(), any(), eq(pageable))).thenReturn(Page.empty());
        Page<PostResponse> result = searchService.searchPosts("none", null, null, null, null, null, null, pageable);
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void suggestTags_success() {
        when(postRepository.findTagsByPrefix("ja")).thenReturn(Collections.singletonList("java"));
        assertFalse(searchService.suggestTags("ja").isEmpty());
    }

    @Test
    void findSimilarPosts_success() {
        Post post = new Post();
        post.setId(1L);
        post.setTags(Collections.emptyList());
        post.setTheme(new com.blog.api.entity.Theme());
        User author = new User();
        author.setId(1L);
        post.setAuthor(author);
        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(post));
        Page<Post> page = new PageImpl<>(Collections.singletonList(post));
        when(postRepository.findSimilarPosts(eq(1L), anyList(), any(), any(Pageable.class))).thenReturn(page);
        when(reactionRepository.countLikesByPost(any())).thenReturn(0L);
        when(reactionRepository.countDislikesByPost(any())).thenReturn(0L);
        Page<PostResponse> result = searchService.findSimilarPosts(1L, Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findSimilarPosts_postNotFound_throwsException() {
        when(postRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(RuntimeException.class, () -> searchService.findSimilarPosts(1L, Pageable.unpaged()));
    }

    @Test
    void search_success() {
        Page<Post> page = new PageImpl<>(Collections.emptyList());
        when(postRepository.findBySearchCriteria(any(), any(), any(), any(), any(), any())).thenReturn(page);
        java.util.Map<String, Object> result = searchService.search("q", "author", "tag", null, null, "date", 0, 10);
        assertNotNull(result);
        assertTrue(result.containsKey("content"));
    }

    @Test
    void search_authorNotFound_success() {
        Page<Post> page = new PageImpl<>(Collections.emptyList());
        when(userRepository.findByUsername("unknown")).thenReturn(java.util.Optional.empty());
        when(postRepository.findBySearchCriteria(any(), isNull(), any(), any(), any(), any())).thenReturn(page);
        java.util.Map<String, Object> result = searchService.search("q", "unknown", "tag", null, null, "date", 0, 10);
        assertNotNull(result);
        assertTrue(result.containsKey("content"));
    }

    @Test
    void search_sortByViews_success() {
        Page<Post> page = new PageImpl<>(Collections.emptyList());
        when(postRepository.findBySearchCriteria(any(), any(), any(), any(), any(), any())).thenReturn(page);
        java.util.Map<String, Object> result = searchService.search("q", null, "tag", null, null, "views", 0, 10);
        assertNotNull(result);
        assertTrue(result.containsKey("content"));
    }

    @Test
    void search_sortByLikes_success() {
        Page<Post> page = new PageImpl<>(Collections.emptyList());
        when(postRepository.findBySearchCriteria(any(), any(), any(), any(), any(), any())).thenReturn(page);
        java.util.Map<String, Object> result = searchService.search("q", null, "tag", null, null, "likes", 0, 10);
        assertNotNull(result);
        assertTrue(result.containsKey("content"));
    }

    @Test
    void search_sortByDefault_success() {
        Page<Post> page = new PageImpl<>(Collections.emptyList());
        when(postRepository.findBySearchCriteria(any(), any(), any(), any(), any(), any())).thenReturn(page);
        // Utilise une valeur inconnue pour tester le default du switch
        java.util.Map<String, Object> result = searchService.search("q", null, "tag", null, null, "unknown", 0, 10);
        assertNotNull(result);
        assertTrue(result.containsKey("content"));
    }

    @Test
    void getPopularTags_success() {
        com.blog.api.entity.Tag tag = new com.blog.api.entity.Tag();
        tag.setName("java");
        tag.setUsageCount(10);
        when(tagRepository.findMostUsedTags(any())).thenReturn(new org.springframework.data.domain.PageImpl<>(Collections.singletonList(tag)));
        java.util.Map<String, Integer> result = searchService.getPopularTags(5);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("java"));
    }

    @Test
    void getSimilarPosts_success() {
        Post post = new Post();
        post.setId(1L);
        post.setTags(Collections.emptyList());
        post.setTheme(new com.blog.api.entity.Theme());
        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(post));
        Page<Post> page = new PageImpl<>(Collections.singletonList(post));
        when(postRepository.findSimilarPosts(eq(1L), anyList(), any(), any(Pageable.class))).thenReturn(page);
        java.util.Map<String, Object> result = searchService.getSimilarPosts(1L, 5);
        assertEquals(1, ((java.util.List)result.get("content")).size());
    }

    @Test
    void getSimilarPosts_postNotFound_throwsException() {
        when(postRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(RuntimeException.class, () -> searchService.getSimilarPosts(1L, 5));
    }
} 