package com.blog.api.service;

import com.blog.api.dto.post.PostResponse;
import com.blog.api.entity.Post;
import com.blog.api.entity.Theme;
import com.blog.api.entity.Tag;
import com.blog.api.entity.User;
import com.blog.api.repository.PostRepository;
import com.blog.api.repository.ReactionRepository;
import com.blog.api.repository.ThemeRepository;
import com.blog.api.repository.TagRepository;
import com.blog.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PostRepository postRepository;
    private final ThemeRepository themeRepository;
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public Page<PostResponse> searchPosts(String keyword, List<Long> themeIds, 
            LocalDateTime startDate, LocalDateTime endDate, List<String> tags, 
            String authorUsername, Post.Status status, Pageable pageable) {
        
        // Construire la requête en fonction des critères
        return postRepository.findBySearchCriteria(
                keyword,
                themeIds,
                startDate,
                endDate,
                tags,
                authorUsername,
                status,
                pageable
            ).map(post -> PostResponse.fromPost(
                post,
                reactionRepository.countLikesByPost(post),
                reactionRepository.countDislikesByPost(post)
            ));
    }

    public List<String> suggestTags(String prefix) {
        return postRepository.findTagsByPrefix(prefix);
    }

    public Page<PostResponse> findSimilarPosts(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return postRepository.findSimilarPosts(
                post.getId(),
                post.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
                post.getTheme().getId(),
                pageable
            ).map(similarPost -> PostResponse.fromPost(
                similarPost,
                reactionRepository.countLikesByPost(similarPost),
                reactionRepository.countDislikesByPost(similarPost)
            ));
    }

    public Map<String, Object> search(
            String query,
            String author,
            String tag,
            LocalDate dateFrom,
            LocalDate dateTo,
            String sortBy,
            int page,
            int size) {

        Pageable pageable = createPageable(sortBy, page, size);
        User authorUser = null;
        if (author != null && !author.isEmpty()) {
            authorUser = userRepository.findByUsername(author)
                    .orElse(null);
        }

        Page<Post> posts = postRepository.findBySearchCriteria(
                query,
                authorUser,
                tag,
                dateFrom,
                dateTo,
                pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", posts.getContent());
        response.put("totalElements", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());
        response.put("currentPage", posts.getNumber());
        
        return response;
    }

    public Map<String, Integer> getPopularTags(int limit) {
        return tagRepository.findMostUsedTags(PageRequest.of(0, limit))
                .stream()
                .collect(Collectors.toMap(
                    Tag::getName,
                    Tag::getUsageCount));
    }

    public Map<String, Object> getSimilarPosts(Long postId, int limit) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Page<Post> similarPosts = postRepository.findSimilarPosts(
                post.getId(),
                post.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
                post.getTheme().getId(),
                PageRequest.of(0, limit));

        Map<String, Object> response = new HashMap<>();
        response.put("content", similarPosts.getContent());
        response.put("totalElements", similarPosts.getTotalElements());
        
        return response;
    }

    private Pageable createPageable(String sortBy, int page, int size) {
        Sort sort = switch (sortBy) {
            case "date" -> Sort.by("createdAt").descending();
            case "views" -> Sort.by("viewCount").descending();
            case "likes" -> Sort.by("likeCount").descending();
            default -> Sort.by("relevanceScore").descending();
        };

        return PageRequest.of(page, size, sort);
    }
} 