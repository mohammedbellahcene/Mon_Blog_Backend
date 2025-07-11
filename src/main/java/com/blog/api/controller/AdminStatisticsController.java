package com.blog.api.controller;

import com.blog.api.service.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatisticsController {
    private final AdminStatisticsService adminStatisticsService;

    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> getGlobalStatistics() {
        return ResponseEntity.ok(adminStatisticsService.getGlobalStatistics());
    }
} 