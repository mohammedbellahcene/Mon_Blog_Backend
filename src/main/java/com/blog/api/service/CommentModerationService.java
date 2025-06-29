package com.blog.api.service;

import com.blog.api.entity.Comment;
import com.blog.api.entity.Notification;
import com.blog.api.entity.User;
import com.blog.api.repository.CommentRepository;
import com.blog.api.repository.UserRepository;
import com.blog.api.security.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CommentModerationService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public Page<Comment> getPendingComments(Pageable pageable) {
        return commentRepository.findByStatus(Comment.ModerationStatus.PENDING, pageable);
    }

    public Page<Comment> getReportedComments(Pageable pageable) {
        return commentRepository.findByReportCountGreaterThanOrderByReportCountDesc(0, pageable);
    }

    @Transactional
    public Comment approveComment(Long commentId, String moderatorUsername) {
        Comment comment = getCommentAndModerator(commentId, moderatorUsername);
        comment.moderate(comment.getModerator(), Comment.ModerationStatus.APPROVED, null);
        
        notificationService.createNotification(
            comment.getAuthor(),
            "Commentaire approuvé",
            "Votre commentaire sur l'article '" + comment.getPost().getTitle() + "' a été approuvé.",
            Notification.NotificationType.SYSTEM,
            "/posts/" + comment.getPost().getId()
        );

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment rejectComment(Long commentId, String moderatorUsername, String reason) {
        Comment comment = getCommentAndModerator(commentId, moderatorUsername);
        comment.moderate(comment.getModerator(), Comment.ModerationStatus.REJECTED, reason);
        
        notificationService.createNotification(
            comment.getAuthor(),
            "Commentaire rejeté",
            "Votre commentaire sur l'article '" + comment.getPost().getTitle() + 
            "' a été rejeté. Raison : " + reason,
            Notification.NotificationType.SYSTEM,
            "/posts/" + comment.getPost().getId()
        );

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment markAsSpam(Long commentId, String moderatorUsername) {
        Comment comment = getCommentAndModerator(commentId, moderatorUsername);
        comment.moderate(comment.getModerator(), Comment.ModerationStatus.SPAM, "Spam détecté");
        
        // Vous pouvez ajouter ici une logique pour bloquer l'utilisateur si nécessaire
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment reportComment(Long commentId, String reporterUsername, String reason) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        comment.incrementReportCount();
        
        // Si le nombre de signalements dépasse un seuil, marquer pour modération
        if (comment.getReportCount() >= 3 && comment.getStatus() == Comment.ModerationStatus.APPROVED) {
            comment.setStatus(Comment.ModerationStatus.PENDING);
        }

        // Notifier les modérateurs
        notifyModerators(comment, reason);
        
        return commentRepository.save(comment);
    }

    private Comment getCommentAndModerator(Long commentId, String moderatorUsername) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new RuntimeException("Moderator not found"));

        String[] roles = moderator.getRoles();
        if (!Arrays.asList(roles).contains(UserRole.ROLE_ADMIN) && 
            !Arrays.asList(roles).contains(UserRole.ROLE_MODERATOR)) {
            throw new RuntimeException("User is not authorized to moderate comments");
        }

        comment.setModerator(moderator);
        return comment;
    }

    private void notifyModerators(Comment comment, String reason) {
        String message = String.format(
            "Nouveau signalement pour un commentaire sur l'article '%s'\n" +
            "Auteur: %s\n" +
            "Raison: %s\n" +
            "Nombre total de signalements: %d",
            comment.getPost().getTitle(),
            comment.getAuthor().getUsername(),
            reason,
            comment.getReportCount()
        );

        // Notifier tous les modérateurs (on récupère les 100 premiers modérateurs)
        Pageable pageable = PageRequest.of(0, 100);
        Page<User> moderators = userRepository.findByRole(UserRole.ROLE_MODERATOR, pageable);
        moderators.forEach(moderator ->
            notificationService.createNotification(
                moderator,
                "Commentaire signalé",
                message,
                Notification.NotificationType.SYSTEM,
                "/admin/moderation"
            )
        );
    }
} 