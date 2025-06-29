package com.blog.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<Post> posts = new ArrayList<>();

    @Formula("(SELECT COUNT(p.id) FROM posts p WHERE p.category_id = id)")
    private Integer postCount;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "icon")
    private String icon;

    @Column(name = "color")
    private String color;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void ensureSlug() {
        if (slug == null || slug.isEmpty()) {
            slug = name.toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-");
        }
    }

    public String getFullPath() {
        if (parent == null) {
            return "/" + slug;
        }
        return parent.getFullPath() + "/" + slug;
    }

    public List<Category> getAncestors() {
        List<Category> ancestors = new ArrayList<>();
        Category current = parent;
        while (current != null) {
            ancestors.add(0, current);
            current = current.getParent();
        }
        return ancestors;
    }

    public boolean isDescendantOf(Category ancestor) {
        Category current = parent;
        while (current != null) {
            if (current.equals(ancestor)) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
} 