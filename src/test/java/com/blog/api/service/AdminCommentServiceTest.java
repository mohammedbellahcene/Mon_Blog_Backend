package com.blog.api.service;

import com.blog.api.entity.Comment;
import com.blog.api.repository.CommentRepository;
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

class AdminCommentServiceTest {
    @Mock private CommentRepository commentRepository;
    @InjectMocks private AdminCommentService adminCommentService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getAllComments_success() {
        Comment comment = new Comment();
        when(commentRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(comment)));
        Page<Comment> result = adminCommentService.getAllComments(Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getCommentById_notFound_returnsEmpty() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertTrue(adminCommentService.getCommentById(1L).isEmpty());
    }

    @Test
    void approveComment_notFound_throwsException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> adminCommentService.approveComment(1L));
    }

    @Test
    void deleteComment_notFound_throwsException() {
        when(commentRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> adminCommentService.deleteComment(1L));
    }

    @Test
    void getReportedComments_success() {
        Comment comment = new Comment();
        when(commentRepository.findByReportCountGreaterThanOrderByReportCountDesc(eq(0), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(comment)));
        Page<Comment> result = adminCommentService.getReportedComments(Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void approveComment_success() {
        Comment comment = new Comment();
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any())).thenReturn(comment);
        Comment result = adminCommentService.approveComment(1L);
        assertEquals(Comment.ModerationStatus.APPROVED, result.getStatus());
    }

    @Test
    void deleteComment_success() {
        when(commentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(commentRepository).deleteById(1L);
        assertDoesNotThrow(() -> adminCommentService.deleteComment(1L));
    }
} 