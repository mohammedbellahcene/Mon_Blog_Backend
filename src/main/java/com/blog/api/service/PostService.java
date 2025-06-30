package com.blog.api.service;

import com.blog.api.dto.post.PostCreateRequest;
import com.blog.api.dto.post.PostResponse;
import com.blog.api.entity.Post;
import com.blog.api.entity.Tag;
import com.blog.api.entity.Theme;
import com.blog.api.entity.User;
import com.blog.api.repository.PostRepository;
import com.blog.api.repository.ReactionRepository;
import com.blog.api.repository.TagRepository;
import com.blog.api.repository.ThemeRepository;
import com.blog.api.repository.UserRepository;
import com.blog.api.security.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final ReactionRepository reactionRepository;
    private final TagRepository tagRepository;

    public Page<PostResponse> getAllPosts(Pageable pageable) {
        return postRepository.findAllOrderByCreatedAtDesc(pageable)
                .map(post -> PostResponse.fromPost(
                        post,
                        reactionRepository.countLikesByPost(post),
                        reactionRepository.countDislikesByPost(post)
                ));
    }

    public Page<PostResponse> getPostsByTheme(Long themeId, Pageable pageable) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RuntimeException("Theme not found"));

        return postRepository.findByTheme(theme, pageable)
                .map(post -> PostResponse.fromPost(
                        post,
                        reactionRepository.countLikesByPost(post),
                        reactionRepository.countDislikesByPost(post)
                ));
    }

    public Page<PostResponse> getPostsByAuthor(String username, Pageable pageable) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return postRepository.findByAuthor(author, pageable)
                .map(post -> PostResponse.fromPost(
                        post,
                        reactionRepository.countLikesByPost(post),
                        reactionRepository.countDislikesByPost(post)
                ));
    }

    public Page<PostResponse> searchPosts(String keyword, Pageable pageable) {
        return postRepository.searchByKeyword(keyword, pageable)
                .map(post -> PostResponse.fromPost(
                        post,
                        reactionRepository.countLikesByPost(post),
                        reactionRepository.countDislikesByPost(post)
                ));
    }

    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return PostResponse.fromPost(
                post,
                reactionRepository.countLikesByPost(post),
                reactionRepository.countDislikesByPost(post)
        );
    }

    @Transactional
    public PostResponse createPost(PostCreateRequest request, String username) {
        try {
            log.info("Création d'un article avec username={}, titre={}, themeId={}", username, request.getTitle(), request.getThemeId());
            User author = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
            log.info("Auteur trouvé: {}", author.getUsername());

            Theme theme = themeRepository.findById(request.getThemeId())
                    .orElseThrow(() -> new RuntimeException("Theme not found with id: " + request.getThemeId()));
            log.info("Thème trouvé: {}", theme.getName());

            Post post = new Post();
            post.setAuthor(author);
            post.setTheme(theme);
            post.setStatus(request.getStatus());
            updatePostFromRequest(post, request);

            Post savedPost = postRepository.save(post);
            log.info("Article créé avec succès, ID: {}", savedPost.getId());
            
            return PostResponse.fromPost(savedPost, 0, 0);
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'article", e);
            throw e;
        }
    }

    @Transactional
    public PostResponse updatePost(Long id, PostCreateRequest request, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!post.getAuthor().equals(author) && !Arrays.asList(author.getRoles()).contains(UserRole.ROLE_ADMIN)) {
            throw new AccessDeniedException("You are not authorized to update this post");
        }

        Theme theme = themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> new RuntimeException("Theme not found"));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setTheme(theme);
        post.setFeaturedImage(request.getFeaturedImage());

        Post updatedPost = postRepository.save(post);
        return PostResponse.fromPost(
                updatedPost,
                reactionRepository.countLikesByPost(updatedPost),
                reactionRepository.countDislikesByPost(updatedPost)
        );
    }

    @Transactional
    public void deletePost(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!post.getAuthor().equals(author) && !Arrays.asList(author.getRoles()).contains(UserRole.ROLE_ADMIN)) {
            throw new AccessDeniedException("You are not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    private void updatePostFromRequest(Post post, PostCreateRequest request) {
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setExcerpt(request.getExcerpt());
        post.setMetaDescription(request.getMetaDescription());
        post.setSlug(request.getSlug());
        post.setFeaturedImage(request.getFeaturedImage());
        post.setFeaturedImageAlt(request.getFeaturedImageAlt());
        post.setFeaturedImageCaption(request.getFeaturedImageCaption());
        post.setOgImage(request.getOgImage());
        post.setGalleryImages(request.getGalleryImages());
        post.setScheduledAt(request.getPublishAt());

        if (request.getThemeId() != null) {
            Theme theme = themeRepository.findById(request.getThemeId())
                    .orElseThrow(() -> new RuntimeException("Theme not found"));
            post.setTheme(theme);
        }

        // Mise à jour des tags
        if (request.getTags() != null) {
            post.getTags().clear();
            request.getTags().forEach(tagName -> {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        });
                post.getTags().add(tag);
            });
        }

        // Calcul du temps de lecture
        post.setReadTime(calculateReadTime(post.getContent()));
    }

    private Integer calculateReadTime(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        // Moyenne de 200 mots par minute
        int wordsPerMinute = 200;
        int wordCount = content.split("\\s+").length;
        return Math.max(1, (int) Math.ceil((double) wordCount / wordsPerMinute));
    }

    public List<PostResponse> getAllPostsAsList() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(post -> PostResponse.fromPost(
                        post,
                        reactionRepository.countLikesByPost(post),
                        reactionRepository.countDislikesByPost(post)
                ))
                .toList();
    }
} 