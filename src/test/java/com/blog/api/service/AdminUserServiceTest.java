package com.blog.api.service;

import com.blog.api.entity.User;
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

class AdminUserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private GlobalStatisticsService globalStatisticsService;
    @InjectMocks private AdminUserService adminUserService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getAllUsers_success() {
        User user = new User();
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(user)));
        Page<User> result = adminUserService.getAllUsers(Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getUserById_notFound_returnsEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertTrue(adminUserService.getUserById(1L).isEmpty());
    }

    @Test
    void setUserEnabled_success() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        User result = adminUserService.setUserEnabled(1L, true);
        assertTrue(result.isEnabled());
    }

    @Test
    void setUserEnabled_userNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> adminUserService.setUserEnabled(1L, true));
    }

    @Test
    void changeUserRoles_success() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        String[] roles = {"ROLE_ADMIN"};
        User result = adminUserService.changeUserRoles(1L, roles);
        assertArrayEquals(roles, result.getRoles());
    }

    @Test
    void changeUserRoles_userNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> adminUserService.changeUserRoles(1L, new String[]{"ROLE_ADMIN"}));
    }

    @Test
    void deleteUser_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);
        doNothing().when(globalStatisticsService).decrementUsers();
        assertDoesNotThrow(() -> adminUserService.deleteUser(1L));
    }

    @Test
    void deleteUser_userNotFound_throwsException() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> adminUserService.deleteUser(1L));
    }
} 