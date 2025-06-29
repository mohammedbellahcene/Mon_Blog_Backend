package com.blog.api.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReportRequest {
    @NotBlank(message = "La raison du signalement est requise")
    private String reason;
} 