package com.blog.api.service;

import com.blog.api.entity.Notification;
import com.blog.api.entity.User;
import com.blog.api.repository.NotificationRepository;
import com.blog.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {
    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private NotificationService notificationService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getUserNotifications_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> notificationService.getUserNotifications("user", Pageable.unpaged()));
    }

    @Test
    void getUserNotifications_success() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        Notification notif = new Notification();
        when(notificationRepository.findByUserOrderByCreatedAtDesc(eq(user), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(notif)));
        Page<Notification> result = notificationService.getUserNotifications("user", Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void createNotification_success() {
        User user = new User();
        Notification notification = new Notification();
        when(notificationRepository.save(any())).thenReturn(notification);
        assertDoesNotThrow(() -> notificationService.createNotification(user, "Titre", "Message", Notification.NotificationType.SYSTEM, "/link"));
    }

    @Test
    void markAsRead_success() {
        User user = new User();
        Notification notif = new Notification();
        notif.setUser(user);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notif));
        when(notificationRepository.save(any())).thenReturn(notif);
        assertDoesNotThrow(() -> notificationService.markAsRead(1L, "user"));
    }

    @Test
    void markAsRead_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> notificationService.markAsRead(1L, "user"));
    }

    @Test
    void markAsRead_notificationNotFound_throwsException() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> notificationService.markAsRead(1L, "user"));
    }

    @Test
    void markAsRead_unauthorized_throwsException() {
        User user = new User();
        user.setId(1L);
        Notification notif = new Notification();
        User other = new User();
        other.setId(2L);
        notif.setUser(other); // autre user
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notif));
        assertThrows(RuntimeException.class, () -> notificationService.markAsRead(1L, "user"));
    }

    @Test
    void markAllAsRead_success() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        doNothing().when(notificationRepository).markAllAsRead(user);
        assertDoesNotThrow(() -> notificationService.markAllAsRead("user"));
    }

    @Test
    void getUnreadCount_success() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(notificationRepository.countByUserAndReadFalse(user)).thenReturn(5L);
        assertEquals(5L, notificationService.getUnreadCount("user"));
    }

    @Test
    void getUnreadCount_userNotFound_throwsException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> notificationService.getUnreadCount("user"));
    }
} 