package com.blog.api.service;

import com.blog.api.dto.comment.CommentRequest;
import com.blog.api.dto.comment.CommentResponse;
import com.blog.api.entity.Comment;
import com.blog.api.entity.Post;
import com.blog.api.entity.User;
import com.blog.api.repository.CommentRepository;
import com.blog.api.repository.PostRepository;
import com.blog.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.security.access.AccessDeniedException;

class CommentServiceTest {
    @Mock private CommentRepository commentRepository;
    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;
    @Mock private GlobalStatisticsService globalStatisticsService;
    @InjectMocks private CommentService commentService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getCommentsByPost_postNotFound_throwsException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> commentService.getCommentsByPost(1L, Pageable.unpaged()));
    }

    @Test
    void getCommentsByPost_success() {
        Post post = new Post();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        Comment comment = new Comment();
        comment.setPost(post);
        User author = new User();
        author.setId(1L);
        author.setUsername("author");
        author.setEmail("author@email.com");
        comment.setAuthor(author);
        when(commentRepository.findByPost(eq(post), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(comment)));
        Page<CommentResponse> result = commentService.getCommentsByPost(1L, Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getCommentsByAuthor_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> commentService.getCommentsByAuthor("user", Pageable.unpaged()));
    }

    @Test
    void getCommentsByAuthor_success() {
        User author = new User();
        author.setId(1L);
        author.setUsername("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(author));
        Comment comment = new Comment();
        comment.setAuthor(author);
        Post post = new Post();
        post.setId(1L);
        comment.setPost(post); // Correction ici
        when(commentRepository.findByAuthor(eq(author), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(comment)));
        Page<CommentResponse> result = commentService.getCommentsByAuthor("user", Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void createComment_postNotFound_throwsException() {
        CommentRequest req = new CommentRequest();
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> commentService.createComment(1L, req, "user"));
    }

    @Test
    void createComment_userNotFound_throwsException() {
        CommentRequest req = new CommentRequest();
        Post post = new Post();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> commentService.createComment(1L, req, "user"));
    }

    @Test
    void createComment_parentNotFound_throwsException() {
        CommentRequest req = new CommentRequest();
        req.setParentId(2L);
        Post post = new Post();
        User author = new User();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(author));
        when(commentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> commentService.createComment(1L, req, "user"));
    }

    @Test
    void createComment_success_withNotifications() {
        CommentRequest req = new CommentRequest();
        req.setContent("test");
        Post post = new Post();
        post.setId(1L);
        User postAuthor = new User();
        postAuthor.setId(2L);
        postAuthor.setUsername("postAuthor");
        post.setAuthor(postAuthor);
        post.setTitle("Titre");
        User author = new User();
        author.setId(1L);
        author.setUsername("user");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(author));
        Comment comment = new Comment();
        comment.setId(10L);
        comment.setAuthor(author);
        comment.setPost(post);
        when(commentRepository.save(any())).thenReturn(comment);
        doNothing().when(globalStatisticsService).incrementComments();
        doNothing().when(notificationService).createNotification(any(), any(), any(), any(), any());
        CommentResponse resp = commentService.createComment(1L, req, "user");
        assertNotNull(resp);
        verify(notificationService, times(1)).createNotification(eq(postAuthor), any(), any(), any(), any());
    }

    @Test
    void createComment_noNotification_whenAuthorIsPostAuthor() {
        CommentRequest req = new CommentRequest();
        req.setContent("test");
        Post post = new Post();
        post.setId(1L);
        User author = new User();
        author.setId(1L);
        author.setUsername("user");
        post.setAuthor(author);
        post.setTitle("Titre");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(author));
        Comment comment = new Comment();
        comment.setId(10L);
        comment.setAuthor(author);
        comment.setPost(post);
        when(commentRepository.save(any())).thenReturn(comment);
        doNothing().when(globalStatisticsService).incrementComments();
        CommentResponse resp = commentService.createComment(1L, req, "user");
        assertNotNull(resp);
        verify(notificationService, never()).createNotification(any(), any(), any(), any(), any());
    }

    @Test
    void createComment_noNotification_whenAuthorIsParentAuthor() {
        CommentRequest req = new CommentRequest();
        req.setContent("test");
        req.setParentId(2L);
        Post post = new Post();
        post.setId(1L);
        User postAuthor = new User();
        postAuthor.setId(2L);
        postAuthor.setUsername("postAuthor");
        post.setAuthor(postAuthor);
        post.setTitle("Titre");
        User author = new User();
        author.setId(1L);
        author.setUsername("user");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(author));
        Comment parent = new Comment();
        parent.setId(2L);
        parent.setAuthor(author); // même auteur que le commentaire
        when(commentRepository.findById(2L)).thenReturn(Optional.of(parent));
        Comment comment = new Comment();
        comment.setId(10L);
        comment.setAuthor(author);
        comment.setPost(post);
        comment.setParent(parent);
        when(commentRepository.save(any())).thenReturn(comment);
        doNothing().when(globalStatisticsService).incrementComments();

        // Capture les appels à createNotification
        org.mockito.ArgumentCaptor<User> userCaptor = org.mockito.ArgumentCaptor.forClass(User.class);
        doNothing().when(notificationService).createNotification(userCaptor.capture(), any(), any(), any(), any());

        CommentResponse resp = commentService.createComment(1L, req, "user");
        assertNotNull(resp);

        // Il doit y avoir une notification envoyée UNIQUEMENT au postAuthor
        assertEquals(1, userCaptor.getAllValues().size());
        assertEquals(postAuthor, userCaptor.getValue());
    }

    @Test
    void updateComment_commentNotFound_throwsException() {
        CommentRequest req = new CommentRequest();
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> commentService.updateComment(1L, req, "user"));
    }

    @Test
    void updateComment_userNotFound_throwsException() {
        CommentRequest req = new CommentRequest();
        Comment comment = new Comment();
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> commentService.updateComment(1L, req, "user"));
    }

    @Test
    void updateComment_accessDenied_throwsException() {
        CommentRequest req = new CommentRequest();
        Comment comment = new Comment();
        User author = new User();
        author.setId(1L);
        author.setUsername("author");
        author.setRoles(new String[]{"ROLE_USER"});
        comment.setAuthor(author);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        User other = new User();
        other.setId(2L);
        other.setUsername("other");
        other.setRoles(new String[]{"ROLE_USER"});
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(other));
        assertThrows(AccessDeniedException.class, () -> commentService.updateComment(1L, req, "other"));
    }

    @Test
    void updateComment_admin_success() {
        CommentRequest req = new CommentRequest();
        req.setContent("edit");
        Comment comment = new Comment();
        Post post = new Post();
        post.setId(1L);
        comment.setPost(post); // Correction ici
        User author = new User();
        author.setId(1L);
        author.setUsername("author");
        author.setRoles(new String[]{"ROLE_USER"});
        comment.setAuthor(author);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        User admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setRoles(new String[]{"ROLE_ADMIN"});
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(commentRepository.save(any())).thenReturn(comment);
        assertDoesNotThrow(() -> commentService.updateComment(1L, req, "admin"));
    }

    @Test
    void deleteComment_commentNotFound_throwsException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> commentService.deleteComment(1L, "user"));
    }

    @Test
    void deleteComment_userNotFound_throwsException() {
        Comment comment = new Comment();
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> commentService.deleteComment(1L, "user"));
    }

    @Test
    void deleteComment_accessDenied_throwsException() {
        Comment comment = new Comment();
        User author = new User();
        author.setId(1L);
        author.setUsername("author");
        author.setRoles(new String[]{"ROLE_USER"});
        comment.setAuthor(author);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        User other = new User();
        other.setId(2L);
        other.setUsername("other");
        other.setRoles(new String[]{"ROLE_USER"});
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(other));
        assertThrows(AccessDeniedException.class, () -> commentService.deleteComment(1L, "other"));
    }

    @Test
    void deleteComment_admin_success() {
        Comment comment = new Comment();
        User author = new User();
        author.setId(1L);
        author.setUsername("author");
        author.setRoles(new String[]{"ROLE_USER"});
        comment.setAuthor(author);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        User admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setRoles(new String[]{"ROLE_ADMIN"});
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        doNothing().when(globalStatisticsService).decrementComments();
        assertDoesNotThrow(() -> commentService.deleteComment(1L, "admin"));
    }

    @Test
    void likeComment_commentNotFound_throwsException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> commentService.likeComment(1L, "user"));
    }

    @Test
    void likeComment_success_withNotification() {
        Comment comment = new Comment();
        comment.setId(1L);
        Post post = new Post();
        post.setId(2L);
        User author = new User();
        author.setId(3L);
        author.setUsername("author");
        post.setAuthor(author);
        post.setTitle("Titre");
        comment.setAuthor(author);
        comment.setPost(post);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any())).thenReturn(comment);
        doNothing().when(notificationService).createNotification(any(), any(), any(), any(), any());
        CommentResponse resp = commentService.likeComment(1L, "otheruser");
        assertNotNull(resp);
        verify(notificationService, times(1)).createNotification(eq(author), any(), any(), any(), any());
    }

    @Test
    void likeComment_noNotification_whenUserIsAuthor() {
        Comment comment = new Comment();
        comment.setId(1L);
        Post post = new Post();
        post.setId(2L);
        User author = new User();
        author.setId(3L);
        author.setUsername("user");
        post.setAuthor(author);
        post.setTitle("Titre");
        comment.setAuthor(author);
        comment.setPost(post);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any())).thenReturn(comment);
        CommentResponse resp = commentService.likeComment(1L, "user");
        assertNotNull(resp);
        verify(notificationService, never()).createNotification(any(), any(), any(), any(), any());
    }

    @Test
    void unlikeComment_commentNotFound_throwsException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> commentService.unlikeComment(1L, "user"));
    }

    @Test
    void unlikeComment_success() {
        Comment comment = new Comment();
        comment.setId(1L);
        Post post = new Post();
        post.setId(1L);
        comment.setPost(post); // déjà présent
        User author = new User();
        author.setId(1L);
        author.setUsername("author");
        comment.setAuthor(author); // Correction ici
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any())).thenReturn(comment);
        CommentResponse resp = commentService.unlikeComment(1L, "user");
        assertNotNull(resp);
    }
} 