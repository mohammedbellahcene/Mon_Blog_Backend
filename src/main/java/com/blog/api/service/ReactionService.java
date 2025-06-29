package com.blog.api.service;

import com.blog.api.dto.reaction.ReactionRequest;
import com.blog.api.dto.reaction.ReactionResponse;
import com.blog.api.dto.reaction.ReactionStatsResponse;
import com.blog.api.entity.Post;
import com.blog.api.entity.Reaction;
import com.blog.api.entity.User;
import com.blog.api.repository.PostRepository;
import com.blog.api.repository.ReactionRepository;
import com.blog.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public ReactionResponse addReaction(Long postId, ReactionRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        Reaction reaction = reactionRepository.findByUserAndPost(user, post)
            .orElse(new Reaction());
        
        reaction.setType(request.getType());
        reaction.setUser(user);
        reaction.setPost(post);
        
        reaction = reactionRepository.save(reaction);
        
        return mapToResponse(reaction);
    }

    @Transactional
    public void deleteReaction(Long postId, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        reactionRepository.deleteByUserAndPost(user, post);
    }

    public ReactionStatsResponse getReactionStats(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        long likesCount = reactionRepository.countByPostAndType(post, Reaction.ReactionType.LIKE);
        long dislikesCount = reactionRepository.countByPostAndType(post, Reaction.ReactionType.DISLIKE);
        double likeRatio = (likesCount + dislikesCount) > 0 
            ? (double) likesCount / (likesCount + dislikesCount) 
            : 0.0;

        return ReactionStatsResponse.builder()
            .postId(postId)
            .likesCount(likesCount)
            .dislikesCount(dislikesCount)
            .likeRatio(likeRatio)
            .build();
    }

    public ReactionResponse getUserReaction(Long postId, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        return reactionRepository.findByUserAndPost(user, post)
            .map(this::mapToResponse)
            .orElse(null);
    }

    private ReactionResponse mapToResponse(Reaction reaction) {
        return ReactionResponse.builder()
            .id(reaction.getId())
            .type(reaction.getType())
            .userId(reaction.getUser().getId())
            .postId(reaction.getPost().getId())
            .createdAt(reaction.getCreatedAt())
            .updatedAt(reaction.getUpdatedAt())
            .build();
    }
} 