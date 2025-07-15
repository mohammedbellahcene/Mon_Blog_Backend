package com.blog.api.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "statistics")
public class GlobalStatistics {
    @Id
    private String id;
    private long totalUsers;
    private long totalPosts;
    private long totalComments;
    private long totalLikes;
    private long totalDislikes;

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalPosts() { return totalPosts; }
    public void setTotalPosts(long totalPosts) { this.totalPosts = totalPosts; }
    public long getTotalComments() { return totalComments; }
    public void setTotalComments(long totalComments) { this.totalComments = totalComments; }
    public long getTotalLikes() { return totalLikes; }
    public void setTotalLikes(long totalLikes) { this.totalLikes = totalLikes; }
    public long getTotalDislikes() { return totalDislikes; }
    public void setTotalDislikes(long totalDislikes) { this.totalDislikes = totalDislikes; }
} 