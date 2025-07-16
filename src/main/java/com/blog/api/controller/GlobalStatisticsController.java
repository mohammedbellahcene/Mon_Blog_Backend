package com.blog.api.controller;

import com.blog.api.entity.GlobalStatistics;
import com.blog.api.service.GlobalStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.blog.api.repository.UserRepository;
import com.blog.api.repository.PostRepository;
import com.blog.api.repository.CommentRepository;
import com.blog.api.repository.ReactionRepository;
import com.blog.api.entity.Reaction;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/mongo/statistics")
@RequiredArgsConstructor
public class GlobalStatisticsController {
    private final GlobalStatisticsService service;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReactionRepository reactionRepository;

    @GetMapping
    public GlobalStatistics getStats() {
        return service.getStats();
    }

    @PostMapping
    public void updateStats(@RequestBody GlobalStatistics stats) {
        service.updateStats(stats);
    }

    @PostMapping("/sync")
    public void syncStats() {
        long totalUsers = userRepository.count();
        long totalPosts = postRepository.count();
        long totalComments = commentRepository.count();
        long totalLikes = reactionRepository.countByType(Reaction.ReactionType.LIKE);
        long totalDislikes = reactionRepository.countByType(Reaction.ReactionType.DISLIKE);
        service.syncAllStats(totalUsers, totalPosts, totalComments, totalLikes, totalDislikes);
    }
} 