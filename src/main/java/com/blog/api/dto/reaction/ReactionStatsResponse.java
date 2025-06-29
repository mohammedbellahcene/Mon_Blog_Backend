package com.blog.api.dto.reaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionStatsResponse {
    private Long postId;
    private Long likesCount;
    private Long dislikesCount;
    private Double likeRatio;
} 