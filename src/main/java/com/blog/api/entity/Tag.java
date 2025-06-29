package com.blog.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @PrePersist
    @PreUpdate
    private void ensureUsageCount() {
        if (usageCount == null) {
            usageCount = 0;
        }
    }
} 