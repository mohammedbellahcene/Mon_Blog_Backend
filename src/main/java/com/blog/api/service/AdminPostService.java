package com.blog.api.service;

import com.blog.api.entity.Post;
import com.blog.api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminPostService {
    private final PostRepository postRepository;

    public Page<Post> getAllPosts(Pageable pageable) {
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findAll(sorted);
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public Post updatePost(Long id, Post updatedPost) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setTheme(updatedPost.getTheme());
        post.setFeaturedImage(updatedPost.getFeaturedImage());
        post.setStatus(updatedPost.getStatus());
        // Ajoute d'autres champs à mettre à jour si besoin
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found");
        }
        postRepository.deleteById(id);
    }

    @Transactional
    public Post setFeatured(Long id, boolean featured) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setFeatured(featured);
        return postRepository.save(post);
    }
} 