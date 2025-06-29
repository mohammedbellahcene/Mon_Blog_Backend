package com.blog.api.controller;

import com.blog.api.dto.comment.CommentModerationRequest;
import com.blog.api.dto.comment.CommentReportRequest;
import com.blog.api.dto.comment.CommentResponse;
import com.blog.api.entity.Comment;
import com.blog.api.service.CommentModerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments/moderation")
@RequiredArgsConstructor
public class CommentModerationController {

    private final CommentModerationService moderationService;

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<CommentResponse>> getPendingComments(Pageable pageable) {
        Page<Comment> comments = moderationService.getPendingComments(pageable);
        return ResponseEntity.ok(comments.map(CommentResponse::fromComment));
    }

    @GetMapping("/reported")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<CommentResponse>> getReportedComments(Pageable pageable) {
        Page<Comment> comments = moderationService.getReportedComments(pageable);
        return ResponseEntity.ok(comments.map(CommentResponse::fromComment));
    }

    @PostMapping("/{commentId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<CommentResponse> approveComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Comment comment = moderationService.approveComment(commentId, userDetails.getUsername());
        return ResponseEntity.ok(CommentResponse.fromComment(comment));
    }

    @PostMapping("/{commentId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<CommentResponse> rejectComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentModerationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Comment comment = moderationService.rejectComment(
            commentId, userDetails.getUsername(), request.getReason());
        return ResponseEntity.ok(CommentResponse.fromComment(comment));
    }

    @PostMapping("/{commentId}/spam")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<CommentResponse> markAsSpam(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Comment comment = moderationService.markAsSpam(commentId, userDetails.getUsername());
        return ResponseEntity.ok(CommentResponse.fromComment(comment));
    }

    @PostMapping("/{commentId}/report")
    public ResponseEntity<CommentResponse> reportComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentReportRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Comment comment = moderationService.reportComment(
            commentId, userDetails.getUsername(), request.getReason());
        return ResponseEntity.ok(CommentResponse.fromComment(comment));
    }
} 