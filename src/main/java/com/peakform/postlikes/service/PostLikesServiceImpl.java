package com.peakform.postlikes.service;

import com.peakform.postlikes.dto.PostLikeResponseDTO;
import com.peakform.postlikes.model.PostLikes;
import com.peakform.postlikes.repository.PostLikesRepository;
import com.peakform.posts.model.Post;
import com.peakform.posts.repository.PostRepository;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikesServiceImpl implements PostLikesService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikesRepository postLikesRepository;

    @Override
    @Transactional
    public PostLikeResponseDTO addLike(Long postId) {

        User user = getCurrentUser();
        Post post = findPostById(postId);

        if (postLikesRepository.existsByUserAndPost(user, post)) {
            long totalLikes = postLikesRepository.countByPost(post);
            return new PostLikeResponseDTO(totalLikes, true);
        }

        PostLikes like = new PostLikes();
        like.setUser(user);
        like.setPost(post);

        postLikesRepository.save(like);
        long newTotalLikes = postLikesRepository.countByPost(post);
        return new PostLikeResponseDTO(newTotalLikes, true);
    }

    @Override
    @Transactional
    public PostLikeResponseDTO removeLike(Long postId) {
        User user = getCurrentUser();
        Post post = findPostById(postId);

        PostLikes like = postLikesRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new EntityNotFoundException("Like not found for this user and post."));

        postLikesRepository.delete(like);

        long newTotalLikes = postLikesRepository.countByPost(post);
        return new PostLikeResponseDTO(newTotalLikes, false);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
    }
}
