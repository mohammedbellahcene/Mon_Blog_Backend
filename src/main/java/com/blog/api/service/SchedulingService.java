package com.blog.api.service;

import com.blog.api.entity.Post;
import com.blog.api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingService {

    private final PostRepository postRepository;

    @Scheduled(fixedRate = 60000) // Vérifie toutes les minutes
    @Transactional
    public void publishScheduledPosts() {
        LocalDateTime now = LocalDateTime.now();
        List<Post> scheduledPosts = postRepository.findByStatusAndPublishedAtBefore(
            Post.Status.SCHEDULED, now);

        for (Post post : scheduledPosts) {
            log.info("Publication programmée de l'article: {}", post.getTitle());
            post.setStatus(Post.Status.PUBLISHED);
            postRepository.save(post);
        }
    }
} 