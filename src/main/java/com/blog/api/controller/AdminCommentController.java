package com.blog.api.controller;

import com.blog.api.entity.Comment;
import com.blog.api.service.AdminCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommentController {
    private final AdminCommentService adminCommentService;

    @GetMapping
    public ResponseEntity<Page<Comment>> getAllComments(Pageable pageable) {
        return ResponseEntity.ok(adminCommentService.getAllComments(pageable));
    }

    @GetMapping("/reported")
    public ResponseEntity<Page<Comment>> getReportedComments(Pageable pageable) {
        return ResponseEntity.ok(adminCommentService.getReportedComments(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        return adminCommentService.getCommentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Comment> approveComment(@PathVariable Long id) {
        return ResponseEntity.ok(adminCommentService.approveComment(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        adminCommentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
} 