package com.blog.api.dto.post;

import com.blog.api.entity.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String excerpt;
    private String metaDescription;
    private String slug;
    private String featuredImage;
    private String featuredImageAlt;
    private String featuredImageCaption;
    private String ogImage;
    private List<String> galleryImages;
    private Integer readTime;
    private AuthorDto author;
    private ThemeDto theme;
    private List<String> tags;
    private long viewCount;
    private long likeCount;
    private long dislikeCount;
    private Post.Status status;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class AuthorDto {
        private Long id;
        private String username;
        private String email;
        private String avatar;
    }

    @Data
    public static class ThemeDto {
        private Long id;
        private String name;
        private String slug;
        private String thumbnail;
    }

    public static PostResponse fromPost(Post post, long likesCount, long dislikesCount) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setExcerpt(post.getExcerpt());
        response.setMetaDescription(post.getMetaDescription());
        response.setSlug(post.getSlug());
        response.setFeaturedImage(post.getFeaturedImage());
        response.setFeaturedImageAlt(post.getFeaturedImageAlt());
        response.setFeaturedImageCaption(post.getFeaturedImageCaption());
        response.setOgImage(post.getOgImage());
        response.setGalleryImages(post.getGalleryImages());
        response.setReadTime(post.getReadTime());
        response.setViewCount(post.getViewCount());
        response.setLikeCount(likesCount);
        response.setDislikeCount(dislikesCount);
        response.setStatus(post.getStatus());
        response.setPublishedAt(post.getPublishedAt());
        response.setScheduledAt(post.getScheduledAt());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());

        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(post.getAuthor().getId());
        authorDto.setUsername(post.getAuthor().getUsername());
        authorDto.setEmail(post.getAuthor().getEmail());
        authorDto.setAvatar(post.getAuthor().getAvatar());
        response.setAuthor(authorDto);

        if (post.getTheme() != null) {
            ThemeDto themeDto = new ThemeDto();
            themeDto.setId(post.getTheme().getId());
            themeDto.setName(post.getTheme().getName());
            themeDto.setSlug(post.getTheme().getSlug());
            themeDto.setThumbnail(post.getTheme().getThumbnail());
            response.setTheme(themeDto);
        }

        response.setTags(post.getTags().stream()
            .map(tag -> tag.getName())
            .collect(Collectors.toList()));

        return response;
    }
} 