package com.blog.api.controller;

import com.blog.api.service.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import com.blog.api.entity.Post;
import com.blog.api.dto.post.PostResponse;
import com.blog.api.repository.PostRepository;
import java.util.List;
import java.util.HashMap;
import com.blog.api.repository.ReactionRepository;

@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatisticsController {
    private final AdminStatisticsService adminStatisticsService;
    private final PostRepository postRepository;
    private final ReactionRepository reactionRepository;

    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> getGlobalStatistics() {
        return ResponseEntity.ok(adminStatisticsService.getGlobalStatistics());
    }

    @GetMapping("/top-posts")
    public ResponseEntity<Map<String, List<PostResponse>>> getTopPosts() {
        Map<String, List<PostResponse>> result = new HashMap<>();

        List<Post> mostViewed = postRepository.findTop5ByOrderByViewCountDesc();
        List<PostResponse> mostViewedResponses = mostViewed.stream()
            .map(post -> PostResponse.fromPost(
                post,
                reactionRepository.countLikesByPost(post),
                reactionRepository.countDislikesByPost(post)
            ))
            .toList();

        List<Post> mostCommented = postRepository.findTop5MostCommented();
        List<PostResponse> mostCommentedResponses = mostCommented.stream()
            .map(post -> PostResponse.fromPost(
                post,
                reactionRepository.countLikesByPost(post),
                reactionRepository.countDislikesByPost(post)
            ))
            .toList();

        List<Post> mostLiked = postRepository.findTop5ByOrderByLikeCountDesc();
        List<PostResponse> mostLikedResponses = mostLiked.stream()
            .map(post -> PostResponse.fromPost(
                post,
                reactionRepository.countLikesByPost(post),
                reactionRepository.countDislikesByPost(post)
            ))
            .toList();

        List<Post> mostDisliked = postRepository.findTop5ByOrderByDislikeCountDesc();
        List<PostResponse> mostDislikedResponses = mostDisliked.stream()
            .map(post -> PostResponse.fromPost(
                post,
                reactionRepository.countLikesByPost(post),
                reactionRepository.countDislikesByPost(post)
            ))
            .toList();

        result.put("mostViewed", mostViewedResponses);
        result.put("mostCommented", mostCommentedResponses);
        result.put("mostLiked", mostLikedResponses);
        result.put("mostDisliked", mostDislikedResponses);
        return ResponseEntity.ok(result);
    }
} 