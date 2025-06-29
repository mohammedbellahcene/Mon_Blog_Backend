package com.blog.api.dto.comment;

import com.blog.api.entity.Comment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private AuthorDto author;
    private Long postId;
    private Long parentId;
    private Comment.ModerationStatus status;
    private String moderationReason;
    private ModeratorDto moderator;
    private LocalDateTime moderatedAt;
    private Integer reportCount;
    private Integer likeCount;
    private boolean isEdited;
    private LocalDateTime editedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class AuthorDto {
        private Long id;
        private String username;
        private String avatar;
    }

    @Data
    public static class ModeratorDto {
        private Long id;
        private String username;
    }

    public static CommentResponse fromComment(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setPostId(comment.getPost().getId());
        response.setStatus(comment.getStatus());
        response.setModerationReason(comment.getModerationReason());
        response.setModeratedAt(comment.getModeratedAt());
        response.setReportCount(comment.getReportCount());
        response.setLikeCount(comment.getLikeCount());
        response.setEdited(comment.isEdited());
        response.setEditedAt(comment.getEditedAt());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());

        if (comment.getParent() != null) {
            response.setParentId(comment.getParent().getId());
        }

        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(comment.getAuthor().getId());
        authorDto.setUsername(comment.getAuthor().getUsername());
        authorDto.setAvatar(comment.getAuthor().getAvatar());
        response.setAuthor(authorDto);

        if (comment.getModerator() != null) {
            ModeratorDto moderatorDto = new ModeratorDto();
            moderatorDto.setId(comment.getModerator().getId());
            moderatorDto.setUsername(comment.getModerator().getUsername());
            response.setModerator(moderatorDto);
        }

        return response;
    }
} 