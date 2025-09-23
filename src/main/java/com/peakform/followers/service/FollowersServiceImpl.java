package com.peakform.followers.service;


import com.peakform.followers.dto.FollowDTO;
import com.peakform.followers.dto.FollowResponseDTO;
import com.peakform.followers.model.Followers;
import com.peakform.followers.repository.FollowersRepository;
import com.peakform.pages.PagedResponse;
import com.peakform.posts.model.Post;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowersServiceImpl implements FollowersService {

    private final FollowersRepository followersRepository;
    private final UserRepository userRepository;

    @Override
    public PagedResponse<FollowDTO> getMyFollowers(Pageable pageable, String username) {

        User user = getCurrentUser();
        User followedUsername = findUserByUsername(username);

        Page<Followers> followersPage;

        if (username != null && !username.isBlank()) {
            followersPage = followersRepository.findByFollowedAndFollower(
                    user, followedUsername, pageable);
        } else {
            followersPage = followersRepository.findByFollowed(user, pageable);
        }

        List<FollowDTO> content = followersPage.stream()
                .map(f -> new FollowDTO(
                        f.getFollower().getUsername(),
                        f.getFollower().getProfileImageUrl()
                ))
                .toList();

        return new PagedResponse<>(
                content,
                followersPage.getNumber(),
                followersPage.getSize(),
                followersPage.getTotalElements(),
                followersPage.getTotalPages(),
                followersPage.isLast()
        );
    }

    @Override
    public PagedResponse<FollowDTO> getMyFollowing(Pageable pageable) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException(currentUsername));

        Page<Followers> followingPage = followersRepository.findByFollower(user, pageable);

        List<FollowDTO> content = followingPage.stream()
                .map(f -> new FollowDTO(
                        f.getFollowed().getUsername(),
                        f.getFollowed().getProfileImageUrl()
                ))
                .toList();

        return new PagedResponse<>(
                content,
                followingPage.getNumber(),
                followingPage.getSize(),
                followingPage.getTotalElements(),
                followingPage.getTotalPages(),
                followingPage.isLast()
        );
    }

    @Override
    @Transactional
    public void followUser(String username) {
        User follower = getCurrentUser();
        User followed = findUserByUsername(username);

        if (follower.getId().equals(followed.getId())) {
            throw new IllegalArgumentException("You cannot follow yourself.");
        }

        followersRepository.findByFollowerAndFollowed(follower, followed)
                .ifPresent(f -> {
                    throw new IllegalStateException("You are already following this user.");
                });

        Followers newFollow = new Followers();
        newFollow.setFollower(follower);
        newFollow.setFollowed(followed);

        followersRepository.save(newFollow);
    }

    @Override
    @Transactional
    public void unfollowUser(String username) {
        User follower = getCurrentUser();
        User followed = findUserByUsername(username);

        Followers followToDelete = followersRepository.findByFollowerAndFollowed(follower, followed)
                .orElseThrow(() -> new IllegalStateException("You are not following this user, so you cannot unfollow them."));

        followersRepository.delete(followToDelete);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
