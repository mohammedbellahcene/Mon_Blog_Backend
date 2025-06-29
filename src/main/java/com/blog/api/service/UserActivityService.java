package com.blog.api.service;

import com.blog.api.entity.Comment;
import com.blog.api.entity.Notification;
import com.blog.api.entity.Post;
import com.blog.api.entity.User;
import com.blog.api.repository.CommentRepository;
import com.blog.api.repository.NotificationRepository;
import com.blog.api.repository.PostRepository;
import com.blog.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;

    public Map<String, Object> getUserActivity(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> activity = new HashMap<>();
        activity.put("posts", getUserPosts(username, page, size));
        activity.put("comments", getUserComments(username, page, size));
        activity.put("notifications", getUserNotifications(username, page, size));

        return activity;
    }

    public Map<String, Object> getUserPosts(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Post> posts = postRepository.findByAuthor(user, Pageable.ofSize(size).withPage(page));
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", posts.getContent());
        response.put("totalElements", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());
        response.put("currentPage", posts.getNumber());
        
        return response;
    }

    public Map<String, Object> getUserComments(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Comment> comments = commentRepository.findByAuthor(user, Pageable.ofSize(size).withPage(page));
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", comments.getContent());
        response.put("totalElements", comments.getTotalElements());
        response.put("totalPages", comments.getTotalPages());
        response.put("currentPage", comments.getNumber());
        
        return response;
    }

    public Map<String, Object> getUserNotifications(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Notification> notifications = notificationRepository.findByUser(user, Pageable.ofSize(size).withPage(page));
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", notifications.getContent());
        response.put("totalElements", notifications.getTotalElements());
        response.put("totalPages", notifications.getTotalPages());
        response.put("currentPage", notifications.getNumber());
        response.put("unreadCount", notificationRepository.countByUserAndReadFalse(user));
        
        return response;
    }

    @Transactional
    public void markNotificationAsRead(String username, Long notificationId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = notificationRepository.findByIdAndUser(notificationId, user)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllNotificationsAsRead(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        notificationRepository.markAllAsRead(user);
    }
} 