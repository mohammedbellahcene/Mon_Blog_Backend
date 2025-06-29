package com.blog.api.dto.comment;

import com.blog.api.entity.Comment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentModerationRequest {
    @NotNull(message = "Le statut est requis")
    private Comment.ModerationStatus status;

    @Size(max = 500, message = "La raison ne doit pas dépasser 500 caractères")
    private String reason;
} 