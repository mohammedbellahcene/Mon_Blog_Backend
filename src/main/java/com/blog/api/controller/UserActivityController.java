package com.blog.api.controller;

import com.blog.api.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserActivityController {

    private final UserActivityService userActivityService;

    @GetMapping("/{username}/activity")
    public ResponseEntity<Map<String, Object>> getUserActivity(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userActivityService.getUserActivity(username, page, size));
    }

    @GetMapping("/{username}/posts")
    public ResponseEntity<Map<String, Object>> getUserPosts(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userActivityService.getUserPosts(username, page, size));
    }

    @GetMapping("/{username}/comments")
    public ResponseEntity<Map<String, Object>> getUserComments(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userActivityService.getUserComments(username, page, size));
    }

    @GetMapping("/{username}/notifications")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getUserNotifications(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userActivityService.getUserNotifications(username, page, size));
    }

    @PostMapping("/{username}/notifications/{notificationId}/read")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable String username,
            @PathVariable Long notificationId) {
        userActivityService.markNotificationAsRead(username, notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{username}/notifications/read-all")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable String username) {
        userActivityService.markAllNotificationsAsRead(username);
        return ResponseEntity.ok().build();
    }
} 