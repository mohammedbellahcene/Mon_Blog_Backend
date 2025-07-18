package com.blog.api.service;

import com.blog.api.entity.Notification;
import com.blog.api.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminNotificationServiceTest {
    @Mock private NotificationRepository notificationRepository;
    @InjectMocks private AdminNotificationService adminNotificationService;
    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getNotificationById_success() {
        Notification notif = new Notification();
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notif));
        assertTrue(adminNotificationService.getNotificationById(1L).isPresent());
    }
    @Test
    void getNotificationById_notFound() {
        when(notificationRepository.findById(2L)).thenReturn(Optional.empty());
        assertFalse(adminNotificationService.getNotificationById(2L).isPresent());
    }
    @Test
    void deleteNotification_notFound_throwsException() {
        when(notificationRepository.existsById(3L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> adminNotificationService.deleteNotification(3L));
    }

    @Test
    void getAllNotifications_success() {
        Notification notif = new Notification();
        org.springframework.data.domain.Page<com.blog.api.entity.Notification> page = new org.springframework.data.domain.PageImpl<>(java.util.Collections.singletonList(notif));
        when(notificationRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        org.springframework.data.domain.Page<com.blog.api.entity.Notification> result = adminNotificationService.getAllNotifications(org.springframework.data.domain.Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void markAsRead_success() {
        Notification notif = new Notification();
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notif));
        when(notificationRepository.save(any())).thenReturn(notif);
        Notification result = adminNotificationService.markAsRead(1L);
        assertTrue(result.isRead());
    }

    @Test
    void markAsRead_notFound_throwsException() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> adminNotificationService.markAsRead(1L));
    }

    @Test
    void deleteNotification_success() {
        when(notificationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(notificationRepository).deleteById(1L);
        assertDoesNotThrow(() -> adminNotificationService.deleteNotification(1L));
    }
} 