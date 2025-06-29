package com.blog.api.dto.reaction;

import com.blog.api.entity.Reaction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReactionRequest {
    @NotNull(message = "Reaction type is required")
    private Reaction.ReactionType type;
} 