package com.peakform.followers.service;


import com.peakform.followers.dto.FollowDTO;
import com.peakform.followers.model.Followers;
import com.peakform.followers.repository.FollowersRepository;
import com.peakform.pages.PagedResponse;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
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

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException(currentUsername));

        Page<Followers> followersPage;

        if (username != null && !username.isBlank()) {
            followersPage = followersRepository.findByFollowedAndFollower_UsernameContainingIgnoreCase(
                    user, username, pageable);
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
}
