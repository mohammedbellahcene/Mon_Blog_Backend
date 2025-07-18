package com.blog.api.service;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.blog.api.repository.*;
import com.blog.api.entity.User;
import com.blog.api.entity.Post;
import com.blog.api.entity.Comment;
import com.blog.api.entity.Notification;

class UserActivityServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private NotificationRepository notificationRepository;
    @InjectMocks private UserActivityService userActivityService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getUserActivity_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userActivityService.getUserActivity("user", 0, 10));
    }

    @Test
    void getUserActivity_success() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        Map<String, Object> posts = new HashMap<>();
        Map<String, Object> comments = new HashMap<>();
        Map<String, Object> notifications = new HashMap<>();
        UserActivityService spyService = spy(userActivityService);
        doReturn(posts).when(spyService).getUserPosts(anyString(), anyInt(), anyInt());
        doReturn(comments).when(spyService).getUserComments(anyString(), anyInt(), anyInt());
        doReturn(notifications).when(spyService).getUserNotifications(anyString(), anyInt(), anyInt());
        Map<String, Object> result = spyService.getUserActivity("user", 0, 10);
        assertNotNull(result.get("posts"));
        assertNotNull(result.get("comments"));
        assertNotNull(result.get("notifications"));
    }

    @Test
    void getUserPosts_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userActivityService.getUserPosts("user", 0, 10));
    }

    @Test
    void getUserPosts_success() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        Post post = new Post();
        List<Post> postList = Collections.singletonList(post);
        Page<Post> page = new PageImpl<>(postList, Pageable.ofSize(10).withPage(0), 1);
        when(postRepository.findByAuthor(eq(user), any(Pageable.class))).thenReturn(page);
        Map<String, Object> result = userActivityService.getUserPosts("user", 0, 10);
        assertEquals(postList, result.get("content"));
        assertEquals(1L, result.get("totalElements"));
    }

    @Test
    void getUserComments_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userActivityService.getUserComments("user", 0, 10));
    }

    @Test
    void getUserComments_success() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        Comment comment = new Comment();
        List<Comment> commentList = Collections.singletonList(comment);
        Page<Comment> page = new PageImpl<>(commentList, Pageable.ofSize(10).withPage(0), 1);
        when(commentRepository.findByAuthor(eq(user), any(Pageable.class))).thenReturn(page);
        Map<String, Object> result = userActivityService.getUserComments("user", 0, 10);
        assertEquals(commentList, result.get("content"));
        assertEquals(1L, result.get("totalElements"));
    }

    @Test
    void getUserNotifications_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userActivityService.getUserNotifications("user", 0, 10));
    }

    @Test
    void getUserNotifications_success() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        Notification notif = new Notification();
        List<Notification> notifList = Collections.singletonList(notif);
        Page<Notification> page = new PageImpl<>(notifList, Pageable.ofSize(10).withPage(0), 1);
        when(notificationRepository.findByUser(eq(user), any(Pageable.class))).thenReturn(page);
        when(notificationRepository.countByUserAndReadFalse(user)).thenReturn(2L);
        Map<String, Object> result = userActivityService.getUserNotifications("user", 0, 10);
        assertEquals(notifList, result.get("content"));
        assertEquals(1L, result.get("totalElements"));
        assertEquals(2L, result.get("unreadCount"));
    }

    @Test
    void markNotificationAsRead_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userActivityService.markNotificationAsRead("user", 1L));
    }

    @Test
    void markNotificationAsRead_notificationNotFound_throwsException() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(notificationRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userActivityService.markNotificationAsRead("user", 1L));
    }

    @Test
    void markNotificationAsRead_success() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        Notification notif = new Notification();
        when(notificationRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(notif));
        when(notificationRepository.save(notif)).thenReturn(notif);
        assertDoesNotThrow(() -> userActivityService.markNotificationAsRead("user", 1L));
        assertTrue(notif.isRead());
    }

    @Test
    void markAllNotificationsAsRead_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userActivityService.markAllNotificationsAsRead("user"));
    }

    @Test
    void markAllNotificationsAsRead_success() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        doNothing().when(notificationRepository).markAllAsRead(user);
        assertDoesNotThrow(() -> userActivityService.markAllNotificationsAsRead("user"));
    }
} 