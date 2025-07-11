package com.blog.api.controller;

import com.blog.api.entity.User;
import com.blog.api.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(adminUserService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return adminUserService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/enabled")
    public ResponseEntity<User> setUserEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        return ResponseEntity.ok(adminUserService.setUserEnabled(id, enabled));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<User> changeUserRoles(@PathVariable Long id, @RequestBody String[] roles) {
        return ResponseEntity.ok(adminUserService.changeUserRoles(id, roles));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
} 