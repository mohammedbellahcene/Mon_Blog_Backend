package com.blog.api.controller;

import com.blog.api.entity.GlobalStatistics;
import com.blog.api.service.GlobalStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mongo/statistics")
@RequiredArgsConstructor
public class GlobalStatisticsController {
    private final GlobalStatisticsService service;

    @GetMapping
    public GlobalStatistics getStats() {
        return service.getStats();
    }

    @PostMapping
    public void updateStats(@RequestBody GlobalStatistics stats) {
        service.updateStats(stats);
    }
} 