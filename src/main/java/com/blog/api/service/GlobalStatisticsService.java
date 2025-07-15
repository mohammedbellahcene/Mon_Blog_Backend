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
} 