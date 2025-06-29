package com.blog.api.controller;

import com.blog.api.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getGlobalStatistics() {
        return ResponseEntity.ok(statisticsService.getGlobalStatistics());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable String username) {
        return ResponseEntity.ok(statisticsService.getUserStatistics(username));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Map<String, Object>> getPostStatistics(@PathVariable Long postId) {
        return ResponseEntity.ok(statisticsService.getPostStatistics(postId));
    }

    @PostMapping("/post/{postId}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long postId) {
        statisticsService.incrementViewCount(postId);
        return ResponseEntity.ok().build();
    }
} 