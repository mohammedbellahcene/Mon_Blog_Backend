package com.blog.api.controller;

import com.blog.api.entity.Post;
import com.blog.api.service.AdminPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {
    private final AdminPostService adminPostService;

    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(adminPostService.getAllPosts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return adminPostService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
        return ResponseEntity.ok(adminPostService.updatePost(id, updatedPost));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        adminPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/featured")
    public ResponseEntity<Post> setFeatured(@PathVariable Long id, @RequestParam boolean featured) {
        return ResponseEntity.ok(adminPostService.setFeatured(id, featured));
    }
} 