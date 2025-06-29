package com.blog.api.service;

import com.blog.api.dto.comment.CommentRequest;
import com.blog.api.dto.comment.CommentResponse;
import com.blog.api.entity.Comment;
import com.blog.api.entity.Notification;
import com.blog.api.entity.Post;
import com.blog.api.entity.User;
import com.blog.api.repository.CommentRepository;
import com.blog.api.repository.PostRepository;
import com.blog.api.repository.UserRepository;
import com.blog.api.security.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return commentRepository.findByPost(post, pageable)
                .map(CommentResponse::fromComment);
    }

    public Page<CommentResponse> getCommentsByAuthor(String username, Pageable pageable) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return commentRepository.findByAuthor(author, pageable)
                .map(CommentResponse::fromComment);
    }

    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setAuthor(author);
        comment.setPost(post);

        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParent(parent);
        }

        Comment savedComment = commentRepository.save(comment);

        // Notifier l'auteur du post
        if (!post.getAuthor().equals(author)) {
            notificationService.createNotification(
                post.getAuthor(),
                "Nouveau commentaire",
                author.getUsername() + " a commenté votre article '" + post.getTitle() + "'",
                Notification.NotificationType.COMMENT,
                "/posts/" + post.getId() + "#comment-" + savedComment.getId()
            );
        }

        // Notifier l'auteur du commentaire parent s'il existe
        if (comment.getParent() != null && !comment.getParent().getAuthor().equals(author)) {
            notificationService.createNotification(
                comment.getParent().getAuthor(),
                "Réponse à votre commentaire",
                author.getUsername() + " a répondu à votre commentaire",
                Notification.NotificationType.COMMENT_REPLY,
                "/posts/" + post.getId() + "#comment-" + savedComment.getId()
            );
        }

        return CommentResponse.fromComment(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest request, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!comment.getAuthor().equals(author) && !Arrays.asList(author.getRoles()).contains(UserRole.ROLE_ADMIN)) {
            throw new AccessDeniedException("You are not authorized to update this comment");
        }

        comment.setContent(request.getContent());
        comment.setEdited(true);
        comment.setEditedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        return CommentResponse.fromComment(updatedComment);
    }

    @Transactional
    public void deleteComment(Long id, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!comment.getAuthor().equals(author) && !Arrays.asList(author.getRoles()).contains(UserRole.ROLE_ADMIN)) {
            throw new AccessDeniedException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public CommentResponse likeComment(Long id, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.incrementLikeCount();
        Comment updatedComment = commentRepository.save(comment);

        // Notifier l'auteur du commentaire
        if (!comment.getAuthor().getUsername().equals(username)) {
            notificationService.createNotification(
                comment.getAuthor(),
                "J'aime sur votre commentaire",
                username + " a aimé votre commentaire",
                Notification.NotificationType.LIKE,
                "/posts/" + comment.getPost().getId() + "#comment-" + comment.getId()
            );
        }

        return CommentResponse.fromComment(updatedComment);
    }

    @Transactional
    public CommentResponse unlikeComment(Long id, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.decrementLikeCount();
        Comment updatedComment = commentRepository.save(comment);
        return CommentResponse.fromComment(updatedComment);
    }
} 