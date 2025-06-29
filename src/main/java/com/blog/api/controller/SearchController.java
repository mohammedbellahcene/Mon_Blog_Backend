package com.blog.api.controller;

import com.blog.api.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "relevance") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        return ResponseEntity.ok(searchService.search(
            query, author, tag, dateFrom, dateTo, sortBy, page, size));
    }

    @GetMapping("/tags/popular")
    public ResponseEntity<Map<String, Integer>> getPopularTags(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(searchService.getPopularTags(limit));
    }

    @GetMapping("/posts/{postId}/similar")
    public ResponseEntity<Map<String, Object>> getSimilarPosts(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(searchService.getSimilarPosts(postId, limit));
    }
} 