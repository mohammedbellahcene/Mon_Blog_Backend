package com.blog.api.service;

import com.blog.api.entity.GlobalStatistics;
import com.blog.api.repository.GlobalStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GlobalStatisticsService {
    private final GlobalStatisticsRepository repository;

    public GlobalStatistics getStats() {
        return repository.findAll().stream().findFirst().orElse(new GlobalStatistics());
    }

    public void updateStats(GlobalStatistics stats) {
        repository.save(stats);
    }

    public void incrementUsers() {
        GlobalStatistics stats = getStats();
        stats.setTotalUsers(stats.getTotalUsers() + 1);
        repository.save(stats);
    }
    public void decrementUsers() {
        GlobalStatistics stats = getStats();
        stats.setTotalUsers(Math.max(0, stats.getTotalUsers() - 1));
        repository.save(stats);
    }
    public void incrementPosts() {
        GlobalStatistics stats = getStats();
        stats.setTotalPosts(stats.getTotalPosts() + 1);
        repository.save(stats);
    }
    public void decrementPosts() {
        GlobalStatistics stats = getStats();
        stats.setTotalPosts(Math.max(0, stats.getTotalPosts() - 1));
        repository.save(stats);
    }
    public void incrementComments() {
        GlobalStatistics stats = getStats();
        stats.setTotalComments(stats.getTotalComments() + 1);
        repository.save(stats);
    }
    public void decrementComments() {
        GlobalStatistics stats = getStats();
        stats.setTotalComments(Math.max(0, stats.getTotalComments() - 1));
        repository.save(stats);
    }
    public void incrementLikes() {
        GlobalStatistics stats = getStats();
        stats.setTotalLikes(stats.getTotalLikes() + 1);
        repository.save(stats);
    }
    public void decrementLikes() {
        GlobalStatistics stats = getStats();
        stats.setTotalLikes(Math.max(0, stats.getTotalLikes() - 1));
        repository.save(stats);
    }
    public void incrementDislikes() {
        GlobalStatistics stats = getStats();
        stats.setTotalDislikes(stats.getTotalDislikes() + 1);
        repository.save(stats);
    }
    public void decrementDislikes() {
        GlobalStatistics stats = getStats();
        stats.setTotalDislikes(Math.max(0, stats.getTotalDislikes() - 1));
        repository.save(stats);
    }

    public void syncAllStats(long totalUsers, long totalPosts, long totalComments, long totalLikes, long totalDislikes) {
        GlobalStatistics stats = getStats();
        stats.setTotalUsers(totalUsers);
        stats.setTotalPosts(totalPosts);
        stats.setTotalComments(totalComments);
        stats.setTotalLikes(totalLikes);
        stats.setTotalDislikes(totalDislikes);
        repository.save(stats);
    }
} 