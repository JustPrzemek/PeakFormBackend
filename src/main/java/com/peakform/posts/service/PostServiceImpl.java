package com.peakform.posts.service;

import com.peakform.claudinary.service.ImageUploadService;
import com.peakform.exceptions.FileTooLargeException;
import com.peakform.followers.repository.FollowersRepository;
import com.peakform.pages.PagedResponse;
import com.peakform.posts.dto.PostDTO;
import com.peakform.posts.enumerate.MediaType;
import com.peakform.posts.model.Post;
import com.peakform.posts.repository.PostRepository;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FollowersRepository followersRepository;
    private final ImageUploadService imageUploadService;

    @Override
    public PagedResponse<PostDTO> getMyPosts(int page, int size) {
        String usernameMe = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(usernameMe)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostDTO> postsPage = postRepository.findPostsWithStatsByUserId(user.getId(), pageable);

        return new PagedResponse<>(
                postsPage.getContent(),
                postsPage.getNumber(),
                postsPage.getSize(),
                postsPage.getTotalElements(),
                postsPage.getTotalPages(),
                postsPage.isLast()
        );
    }

    @Override
    public PagedResponse<PostDTO> getFeed(int page, int size) {
        String usernameMe = SecurityContextHolder.getContext().getAuthentication().getName();
        User me = userRepository.findByUsername(usernameMe)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostDTO> postsPage = postRepository.findPostsFromFollowedUsers(me.getId(), pageable);

        return new PagedResponse<>(
                postsPage.getContent(),
                postsPage.getNumber(),
                postsPage.getSize(),
                postsPage.getTotalElements(),
                postsPage.getTotalPages(),
                postsPage.isLast()
        );
    }

    @Override
    public PagedResponse<PostDTO> getUserPosts(String username, int page, int size) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User me = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User profileOwner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isFollowing = followersRepository.existsByFollowerIdAndFollowedId(me.getId(), profileOwner.getId());

        if (!isFollowing) {
            return new PagedResponse<>(Collections.emptyList(), page, size, 0, 0, true);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostDTO> postsPage = postRepository.findPostsWithStatsByUsername(username, pageable);

        return new PagedResponse<>(
                postsPage.getContent(),
                postsPage.getNumber(),
                postsPage.getSize(),
                postsPage.getTotalElements(),
                postsPage.getTotalPages(),
                postsPage.isLast()
        );
    }

    @Override
    public String createPost(String content, MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setUser(user);
        post.setContent(content);

        if (file != null && !file.isEmpty()) {

            long maxSizeInBytes = 50 * 1024 * 1024; // 50 MB
            if (file.getSize() > maxSizeInBytes) {
                throw new FileTooLargeException("File size exceeds the limit of 50 MB");
            }

            Map<String, Object> uploadResult = imageUploadService.uploadFile(file);

            String url = (String) uploadResult.get("secure_url");
            String resourceType = (String) uploadResult.get("resource_type");

            post.setMediaUrl(url);

            if ("image".equals(resourceType)) {
                post.setMediaType(MediaType.IMAGE);
            } else if ("video".equals(resourceType)) {
                post.setMediaType(MediaType.VIDEO);
            }
        }

        postRepository.save(post);
        return "Post created successfully with ID: " + post.getId();
    }
}
