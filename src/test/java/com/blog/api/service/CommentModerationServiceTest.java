package com.blog.api.service;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.Collections;

class CommentModerationServiceTest {
    @Mock private com.blog.api.repository.CommentRepository commentRepository;
    @Mock private com.blog.api.repository.UserRepository userRepository;
    @Mock private NotificationService notificationService;
    @InjectMocks private CommentModerationService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getPendingComments_success() {
        com.blog.api.entity.Comment comment = new com.blog.api.entity.Comment();
        when(commentRepository.findByStatus(any(), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(comment)));
        Page<com.blog.api.entity.Comment> result = service.getPendingComments(Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getReportedComments_success() {
        com.blog.api.entity.Comment comment = new com.blog.api.entity.Comment();
        when(commentRepository.findByReportCountGreaterThanOrderByReportCountDesc(eq(0), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(comment)));
        Page<com.blog.api.entity.Comment> result = service.getReportedComments(Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void approveComment_success() {
        com.blog.api.entity.Comment comment = new com.blog.api.entity.Comment();
        com.blog.api.entity.Post post = new com.blog.api.entity.Post();
        post.setTitle("Titre");
        comment.setPost(post);
        com.blog.api.entity.User moderator = new com.blog.api.entity.User();
        moderator.setRoles(new String[]{"ROLE_ADMIN"});
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("mod")).thenReturn(Optional.of(moderator));
        when(commentRepository.save(any())).thenReturn(comment);
        doNothing().when(notificationService).createNotification(any(), any(), any(), any(), any());
        assertDoesNotThrow(() -> service.approveComment(1L, "mod"));
    }

    @Test
    void approveComment_commentNotFound_throwsException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.approveComment(1L, "mod"));
    }

    @Test
    void approveComment_moderatorNotFound_throwsException() {
        com.blog.api.entity.Comment comment = new com.blog.api.entity.Comment();
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("mod")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.approveComment(1L, "mod"));
    }

    @Test
    void approveComment_notAuthorized_throwsException() {
        com.blog.api.entity.Comment comment = new com.blog.api.entity.Comment();
        com.blog.api.entity.User moderator = new com.blog.api.entity.User();
        moderator.setRoles(new String[]{"ROLE_USER"});
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("mod")).thenReturn(Optional.of(moderator));
        assertThrows(RuntimeException.class, () -> service.approveComment(1L, "mod"));
    }

    @Test
    void approveComment_moderatorRoleModerator_success() {
        com.blog.api.entity.Comment comment = new com.blog.api.entity.Comment();
        com.blog.api.entity.Post post = new com.blog.api.entity.Post();
        post.setTitle("Titre");
        comment.setPost(post);
        com.blog.api.entity.User moderator = new com.blog.api.entity.User();
        moderator.setRoles(new String[]{"ROLE_MODERATOR"});
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("mod")).thenReturn(Optional.of(moderator));
        when(commentRepository.save(any())).thenReturn(comment);
        doNothing().when(notificationService).createNotification(any(), any(), any(), any(), any());
        assertDoesNotThrow(() -> service.approveComment(1L, "mod"));
    }

    @Test
    void rejectComment_success() {
        com.blog.api.entity.Comment comment = new com.blog.api.entity.Comment();
        com.blog.api.entity.Post post = new com.blog.api.entity.Post();
        post.setTitle("Titre");
        comment.setPost(post);
        com.blog.api.entity.User moderator = new com.blog.api.entity.User();
        moderator.setRoles(new String[]{"ROLE_ADMIN"});
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("mod")).thenReturn(Optional.of(moderator));
        when(commentRepository.save(any())).thenReturn(comment);
        doNothing().when(notificationService).createNotification(any(), any(), any(), any(), any());
        assertDoesNotThrow(() -> service.rejectComment(1L, "mod", "reason"));
    }

    @Test
    void rejectComment_moderatorRoleModerator_success() {
        com.blog.api.entity.Comment comment = new com.blog.api.entity.Comment();
        com.blog.api.entity.Post post = new com.blog.api.entity.Post();
        post.setTitle("Titre");
        comment.setPost(post);
        com.blog.api.entity.User moderator = new com.blog.api.entity.User();
        moderator.setRoles(new String[]{"ROLE_MODERATOR"});
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("mod")).thenReturn(Optional.of(moderator));
        when(commentRepository.save(any())).thenReturn(comment);
        doNothing().when(notificationService).createNotification(any(), any(), any(), any(), any());
        assertDoesNotThrow(() -> service.rejectComment(1L, "mod", "reason"));
    }

    @Test
    void markAsSpam_success() {
        com.blog.api.entity.Comment comment = new com.blog.api.entity.Comment();
        com.blog.api.entity.User moderator = new com.blog.api.entity.User();
        moderator.setRoles(new String[]{"ROLE_ADMIN"});
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("mod")).thenReturn(Optional.of(moderator));
        when(commentRepository.save(any())).thenReturn(comment);
        assertDoesNotThrow(() -> service.markAsSpam(1L, "mod"));
    }

    @Test
    void markAsSpam_moderatorRoleModerator_success() {
        com.blog.api.entity.Comment comment = new com.blog.api.entity.Comment();
        com.blog.api.entity.User moderator = new com.blog.api.entity.User();
        moderator.setRoles(new String[]{"ROLE_MODERATOR"});
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("mod")).thenReturn(Optional.of(moderator));
        when(commentRepository.save(any())).thenReturn(comment);
        assertDoesNotThrow(() -> service.markAsSpam(1L, "mod"));
    }

    @Test
    void reportComment_success() {
        com.blog.api.entity.Comment comment = new com.blog.api.entity.Comment();
        com.blog.api.entity.Post post = new com.blog.api.entity.Post();
        post.setTitle("Titre");
        comment.setPost(post);
        com.blog.api.entity.User author = new com.blog.api.entity.User();
        author.setUsername("author");
        comment.setAuthor(author);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any())).thenReturn(comment);
        when(userRepository.findByRole(any(), any())).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList()));
        doNothing().when(notificationService).createNotification(any(), any(), any(), any(), any());
        assertDoesNotThrow(() -> service.reportComment(1L, "user", "reason"));
    }

    @Test
    void reportComment_commentNotFound_throwsException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.reportComment(1L, "user", "reason"));
    }

    @Test
    void reportComment_statusGoesPending() {
        com.blog.api.entity.Comment comment = new com.blog.api.entity.Comment();
        comment.setReportCount(2);
        comment.setStatus(com.blog.api.entity.Comment.ModerationStatus.APPROVED);
        com.blog.api.entity.Post post = new com.blog.api.entity.Post();
        post.setTitle("Titre");
        comment.setPost(post);
        com.blog.api.entity.User author = new com.blog.api.entity.User();
        author.setUsername("author");
        comment.setAuthor(author);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findByRole(any(), any())).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList()));
        doNothing().when(notificationService).createNotification(any(), any(), any(), any(), any());
        service.reportComment(1L, "user", "reason");
        assertEquals(com.blog.api.entity.Comment.ModerationStatus.PENDING, comment.getStatus());
    }
} 