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
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class FollowersServiceImpl implements FollowersService {

    private final FollowersRepository followersRepository;
    private final UserRepository userRepository;

    @Override
    public PagedResponse<FollowDTO> getMyFollowers(Pageable pageable, String search) {
        User currentUser = getCurrentUser();
        return getUserFollowers(currentUser.getUsername(), pageable, search);
    }

    @Override
    public PagedResponse<FollowDTO> getMyFollowing(Pageable pageable, String search) {
        User currentUser = getCurrentUser();
        return getUserFollowing(currentUser.getUsername(), pageable, search);
    }

    @Override
    public PagedResponse<FollowDTO> getUserFollowers(String username, Pageable pageable, String search) {
        User user = findUserByUsername(username);
        Page<Followers> followersPage;

        if (search != null && !search.isBlank()) {
            followersPage = followersRepository.findByFollowedAndFollower_UsernameContainingIgnoreCase(user, search, pageable);
        } else {
            followersPage = followersRepository.findByFollowed(user, pageable);
        }

        return mapToPagedResponse(followersPage, Followers::getFollower);
    }

    @Override
    public PagedResponse<FollowDTO> getUserFollowing(String username, Pageable pageable, String search) {
        User user = findUserByUsername(username);
        Page<Followers> followingPage;

        if (search != null && !search.isBlank()) {
            followingPage = followersRepository.findByFollowerAndFollowed_UsernameContainingIgnoreCase(user, search, pageable);
        } else {
            followingPage = followersRepository.findByFollower(user, pageable);
        }

        return mapToPagedResponse(followingPage, Followers::getFollowed);
    }

    @Override
    @Transactional
    public void followUser(String usernameToFollow) {
        User follower = getCurrentUser();
        User followed = findUserByUsername(usernameToFollow);

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
    public void unfollowUser(String usernameToUnfollow) {
        User follower = getCurrentUser();
        User followed = findUserByUsername(usernameToUnfollow);

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
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private PagedResponse<FollowDTO> mapToPagedResponse(Page<Followers> page, Function<Followers, User> userExtractor) {
        List<FollowDTO> content = page.getContent().stream()
                .map(userExtractor)
                .map(user -> new FollowDTO(user.getUsername(), user.getProfileImageUrl()))
                .toList();

        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
