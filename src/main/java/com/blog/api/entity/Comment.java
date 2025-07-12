package com.blog.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(name = "moderation_status")
    @Enumerated(EnumType.STRING)
    private ModerationStatus status = ModerationStatus.PENDING;

    @Column(name = "moderation_reason")
    private String moderationReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @Column(name = "moderated_at")
    private LocalDateTime moderatedAt;

    @Column(name = "report_count")
    private Integer reportCount = 0;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Column(name = "is_edited")
    private boolean isEdited = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum ModerationStatus {
        PENDING,
        APPROVED,
        REJECTED,
        SPAM
    }

    @PreRemove
    private void preRemove() {
        if (author != null) {
            author.getComments().remove(this);
        }
    }

    public void incrementReportCount() {
        if (this.reportCount == null) {
            this.reportCount = 0;
        }
        this.reportCount++;
    }

    public void incrementLikeCount() {
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount == null || this.likeCount <= 0) {
            this.likeCount = 0;
        } else {
            this.likeCount--;
        }
    }

    public void moderate(User moderator, ModerationStatus status, String reason) {
        this.moderator = moderator;
        this.status = status;
        this.moderationReason = reason;
        this.moderatedAt = LocalDateTime.now();
    }
} 