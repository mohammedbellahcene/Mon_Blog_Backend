package com.blog.api.service;

import com.blog.api.dto.post.PostCreateRequest;
import com.blog.api.dto.post.PostResponse;
import com.blog.api.entity.Post;
import com.blog.api.entity.Theme;
import com.blog.api.entity.User;
import com.blog.api.repository.PostRepository;
import com.blog.api.repository.ReactionRepository;
import com.blog.api.repository.TagRepository;
import com.blog.api.repository.ThemeRepository;
import com.blog.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;

class PostServiceTest {
    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private ThemeRepository themeRepository;
    @Mock private ReactionRepository reactionRepository;
    @Mock private TagRepository tagRepository;
    @Mock private GlobalStatisticsService globalStatisticsService;
    @InjectMocks private PostService postService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getAllPosts_success() {
        Post post = new Post();
        User author = new User();
        author.setId(1L);
        post.setAuthor(author);
        when(postRepository.findAllOrderByCreatedAtDesc(any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(post)));
        when(reactionRepository.countLikesByPost(post)).thenReturn(1L);
        when(reactionRepository.countDislikesByPost(post)).thenReturn(0L);
        Page<PostResponse> result = postService.getAllPosts(Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getPostsByTheme_themeNotFound_throwsException() {
        when(themeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.getPostsByTheme(1L, Pageable.unpaged()));
    }

    @Test
    void getPostsByTheme_success() {
        Theme theme = new Theme();
        when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
        Post post = new Post();
        User author = new User();
        author.setId(1L);
        post.setAuthor(author);
        when(postRepository.findByTheme(eq(theme), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(post)));
        when(reactionRepository.countLikesByPost(post)).thenReturn(1L);
        when(reactionRepository.countDislikesByPost(post)).thenReturn(0L);
        Page<PostResponse> result = postService.getPostsByTheme(1L, Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getPostsByAuthor_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.getPostsByAuthor("user", Pageable.unpaged()));
    }

    @Test
    void getPostsByAuthor_success() {
        User author = new User();
        author.setId(1L);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(author));
        Post post = new Post();
        post.setAuthor(author);
        when(postRepository.findByAuthor(eq(author), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(post)));
        when(reactionRepository.countLikesByPost(post)).thenReturn(1L);
        when(reactionRepository.countDislikesByPost(post)).thenReturn(0L);
        Page<PostResponse> result = postService.getPostsByAuthor("user", Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void createPost_userNotFound_throwsException() {
        PostCreateRequest req = new PostCreateRequest();
        req.setThemeId(1L);
        req.setTitle("title");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.createPost(req, "unknown"));
    }

    @Test
    void createPost_themeNotFound_throwsException() {
        PostCreateRequest req = new PostCreateRequest();
        req.setThemeId(99L);
        req.setTitle("title");
        User author = new User();
        when(userRepository.findByUsername("author")).thenReturn(Optional.of(author));
        when(themeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.createPost(req, "author"));
    }

    @Test
    void createPost_saveException_propagates() {
        PostCreateRequest req = new PostCreateRequest();
        req.setThemeId(1L);
        req.setTitle("title");
        User author = new User();
        when(userRepository.findByUsername("author")).thenReturn(Optional.of(author));
        when(themeRepository.findById(1L)).thenReturn(Optional.of(new Theme()));
        when(postRepository.save(any())).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> postService.createPost(req, "author"));
    }

    @Test
    void updatePost_accessDenied_throwsException() {
        Post post = new Post();
        post.setId(1L);
        User author = new User();
        author.setId(10L);
        author.setUsername("testuser");
        post.setAuthor(author);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        User otherUser = new User();
        otherUser.setId(20L);
        otherUser.setUsername("otheruser");
        otherUser.setRoles(new String[]{"ROLE_USER"});
        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherUser));
        PostCreateRequest req = new PostCreateRequest();
        req.setThemeId(1L);
        assertThrows(AccessDeniedException.class, () -> postService.updatePost(1L, req, "otheruser"));
    }

    @Test
    void updatePost_postNotFound_throwsException() {
        PostCreateRequest req = new PostCreateRequest();
        when(postRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.updatePost(99L, req, "user"));
    }

    @Test
    void updatePost_authorNull_throwsException() {
        Post post = new Post();
        post.setId(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        PostCreateRequest req = new PostCreateRequest();
        assertThrows(RuntimeException.class, () -> postService.updatePost(1L, req, "user"));
    }

    @Test
    void updatePost_themeNotFound_throwsException() {
        Post post = new Post();
        post.setId(1L);
        User author = new User();
        author.setUsername("author");
        author.setRoles(new String[]{"ROLE_ADMIN"});
        post.setAuthor(author);
        PostCreateRequest req = new PostCreateRequest();
        req.setTitle("title");
        req.setThemeId(99L);
        req.setContent("content");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("author")).thenReturn(Optional.of(author));
        when(themeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.updatePost(1L, req, "author"));
    }

    @Test
    void deletePost_accessDenied_throwsException() {
        Post post = new Post();
        post.setId(1L);
        User author = new User();
        author.setId(10L);
        author.setUsername("testuser");
        post.setAuthor(author);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        User otherUser = new User();
        otherUser.setId(20L);
        otherUser.setUsername("otheruser");
        otherUser.setRoles(new String[]{"ROLE_USER"});
        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherUser));
        assertThrows(AccessDeniedException.class, () -> postService.deletePost(1L, "otheruser"));
    }

    @Test
    void deletePost_postNotFound_throwsException() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.deletePost(99L, "user"));
    }

    @Test
    void deletePost_authorNull_throwsException() {
        Post post = new Post();
        post.setId(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.deletePost(1L, "user"));
    }

    @Test
    void updatePostFromRequest_tagsNull_contentNull() throws Exception {
        Post post = new Post();
        PostCreateRequest req = new PostCreateRequest();
        req.setTitle("title");
        req.setContent(null);
        req.setThemeId(null);
        req.setTags(null);
        java.lang.reflect.Method m = PostService.class.getDeclaredMethod("updatePostFromRequest", Post.class, PostCreateRequest.class);
        m.setAccessible(true);
        m.invoke(postService, post, req);
    }

    @Test
    void updatePostFromRequest_themeIdNotNull_themeNotFound_throwsException() throws Exception {
        Post post = new Post();
        PostCreateRequest req = new PostCreateRequest();
        req.setThemeId(123L);
        when(themeRepository.findById(123L)).thenReturn(Optional.empty());
        java.lang.reflect.Method m = PostService.class.getDeclaredMethod("updatePostFromRequest", Post.class, PostCreateRequest.class);
        m.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> m.invoke(postService, post, req));
        assertTrue(ex.getCause().getMessage().contains("Theme not found"));
    }

    @Test
    void updatePostFromRequest_tagsNonNull_tagExists() throws Exception {
        Post post = new Post();
        PostCreateRequest req = new PostCreateRequest();
        req.setTags(java.util.Arrays.asList("tag1"));
        com.blog.api.entity.Tag tag = new com.blog.api.entity.Tag();
        tag.setName("tag1");
        when(tagRepository.findByName("tag1")).thenReturn(Optional.of(tag));
        java.lang.reflect.Method m = PostService.class.getDeclaredMethod("updatePostFromRequest", Post.class, PostCreateRequest.class);
        m.setAccessible(true);
        m.invoke(postService, post, req);
    }

    @Test
    void updatePostFromRequest_tagsNonNull_tagNotExists() throws Exception {
        Post post = new Post();
        PostCreateRequest req = new PostCreateRequest();
        req.setTags(java.util.Arrays.asList("tag2"));
        when(tagRepository.findByName("tag2")).thenReturn(Optional.empty());
        when(tagRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        java.lang.reflect.Method m = PostService.class.getDeclaredMethod("updatePostFromRequest", Post.class, PostCreateRequest.class);
        m.setAccessible(true);
        m.invoke(postService, post, req);
    }

    @Test
    void calculateReadTime_veryLongText() throws Exception {
        java.lang.reflect.Method m = PostService.class.getDeclaredMethod("calculateReadTime", String.class);
        m.setAccessible(true);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) sb.append("mot ");
        int result = (int) m.invoke(postService, sb.toString());
        assertTrue(result > 10);
    }

    @Test
    void calculateReadTime_manySpaces() throws Exception {
        java.lang.reflect.Method m = PostService.class.getDeclaredMethod("calculateReadTime", String.class);
        m.setAccessible(true);
        String text = "mot      mot   mot";
        int result = (int) m.invoke(postService, text);
        assertEquals(1, result);
    }

    @Test
    void getPostById_success_incrementsViewCount() {
        Post post = new Post();
        post.setId(1L);
        post.setViewCount(0);
        User author = new User();
        author.setId(1L);
        post.setAuthor(author);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(reactionRepository.countLikesByPost(post)).thenReturn(0L);
        when(reactionRepository.countDislikesByPost(post)).thenReturn(0L);
        when(postRepository.save(any())).thenReturn(post);
        PostResponse resp = postService.getPostById(1L);
        assertEquals(1, post.getViewCount());
        assertNotNull(resp);
    }

    @Test
    void getPostById_notFound_throwsException() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.getPostById(99L));
    }

    @Test
    void searchPosts_success() {
        Post post = new Post();
        User author = new User();
        author.setId(1L);
        post.setAuthor(author);
        when(postRepository.searchByKeyword(eq("test"), any())).thenReturn(new PageImpl<>(Collections.singletonList(post)));
        when(reactionRepository.countLikesByPost(post)).thenReturn(0L);
        when(reactionRepository.countDislikesByPost(post)).thenReturn(0L);
        Page<PostResponse> result = postService.searchPosts("test", Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchPosts_noResults() {
        when(postRepository.searchByKeyword(eq("none"), any())).thenReturn(Page.empty());
        Page<PostResponse> result = postService.searchPosts("none", Pageable.unpaged());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getAllPostsAsList_success() {
        Post post = new Post();
        User author = new User();
        author.setId(1L);
        post.setAuthor(author);
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Collections.singletonList(post));
        when(reactionRepository.countLikesByPost(post)).thenReturn(0L);
        when(reactionRepository.countDislikesByPost(post)).thenReturn(0L);
        List<PostResponse> result = postService.getAllPostsAsList();
        assertEquals(1, result.size());
    }

    @Test
    void getAllPostsAsList_empty() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());
        List<PostResponse> result = postService.getAllPostsAsList();
        assertEquals(0, result.size());
    }

    @Test
    void updatePostFromRequest_tagsEmpty_themeIdNull() throws Exception {
        Post post = new Post();
        PostCreateRequest req = new PostCreateRequest();
        req.setTags(Collections.emptyList());
        req.setThemeId(null);
        java.lang.reflect.Method m = PostService.class.getDeclaredMethod("updatePostFromRequest", Post.class, PostCreateRequest.class);
        m.setAccessible(true);
        m.invoke(postService, post, req);
    }

    @Test
    void updatePostFromRequest_themeIdNotFound_throwsException() throws Exception {
        Post post = new Post();
        PostCreateRequest req = new PostCreateRequest();
        req.setThemeId(123L);
        when(themeRepository.findById(123L)).thenReturn(Optional.empty());
        java.lang.reflect.Method m = PostService.class.getDeclaredMethod("updatePostFromRequest", Post.class, PostCreateRequest.class);
        m.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> m.invoke(postService, post, req));
        assertTrue(ex.getCause().getMessage().contains("Theme not found"));
    }

    @Test
    void updatePostFromRequest_tagsNonNull_tagAlreadyExists() throws Exception {
        Post post = new Post();
        PostCreateRequest req = new PostCreateRequest();
        req.setTags(Arrays.asList("tag1"));
        com.blog.api.entity.Tag tag = new com.blog.api.entity.Tag();
        tag.setName("tag1");
        when(tagRepository.findByName("tag1")).thenReturn(Optional.of(tag));
        java.lang.reflect.Method m = PostService.class.getDeclaredMethod("updatePostFromRequest", Post.class, PostCreateRequest.class);
        m.setAccessible(true);
        m.invoke(postService, post, req);
    }

    @Test
    void updatePost_accessAdmin_success() {
        Post post = new Post();
        post.setId(1L);
        User author = new User();
        author.setId(10L);
        author.setUsername("testuser");
        post.setAuthor(author);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        User admin = new User();
        admin.setId(99L);
        admin.setUsername("admin");
        admin.setRoles(new String[]{"ROLE_ADMIN"});
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        Theme theme = new Theme();
        when(themeRepository.findById(any())).thenReturn(Optional.of(theme));
        PostCreateRequest req = new PostCreateRequest();
        req.setThemeId(1L);
        req.setTitle("new title");
        req.setContent("new content");
        req.setFeaturedImage("img.png");
        Post updated = new Post();
        updated.setAuthor(admin); // Correction ici
        when(postRepository.save(any())).thenReturn(updated);
        assertDoesNotThrow(() -> postService.updatePost(1L, req, "admin"));
    }

    @Test
    void updatePostFromRequest_tagsNull_themeIdNull() throws Exception {
        Post post = new Post();
        PostCreateRequest req = new PostCreateRequest();
        req.setTags(null);
        req.setThemeId(null);
        java.lang.reflect.Method m = PostService.class.getDeclaredMethod("updatePostFromRequest", Post.class, PostCreateRequest.class);
        m.setAccessible(true);
        m.invoke(postService, post, req);
    }

   
    @Test
    void calculateReadTime_nullOrEmpty() throws Exception {
        java.lang.reflect.Method m = PostService.class.getDeclaredMethod("calculateReadTime", String.class);
        m.setAccessible(true);
        assertEquals(0, m.invoke(postService, (String) null));
        assertEquals(0, m.invoke(postService, ""));
    }

    @Test
    void calculateReadTime_shortText() throws Exception {
        java.lang.reflect.Method m = PostService.class.getDeclaredMethod("calculateReadTime", String.class);
        m.setAccessible(true);
        String text = "mot mot mot";
        int result = (int) m.invoke(postService, text);
        assertEquals(1, result);
    }

    @Test
    void createPost_catchException_logsAndThrows() {
        PostCreateRequest req = new PostCreateRequest();
        req.setThemeId(1L);
        req.setTitle("title");
        when(userRepository.findByUsername("user")).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> postService.createPost(req, "user"));
    }
} 