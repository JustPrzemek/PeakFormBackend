package com.peakform.followers.controller;

import com.peakform.followers.dto.FollowDTO;
import com.peakform.followers.dto.FollowResponseDTO;
import com.peakform.followers.service.FollowersService;
import com.peakform.pages.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class FollowersControllerImpl implements FollowersController {

    private final FollowersService followersService;

    @Override
    public ResponseEntity<PagedResponse<FollowDTO>> getMyFollowers(Pageable pageable, String username) {
        return ResponseEntity.ok(followersService.getMyFollowers(pageable, username));
    }

    @Override
    public ResponseEntity<PagedResponse<FollowDTO>> getMyFollowing(Pageable pageable) {
        return ResponseEntity.ok(followersService.getMyFollowing(pageable));
    }

    @Override
    public ResponseEntity<Void> followUser(String usernameToFollow) {
        followersService.followUser(usernameToFollow);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> unfollowUser(String usernameToUnfollow) {
        followersService.unfollowUser(usernameToUnfollow);
        return ResponseEntity.noContent().build();
    }
}
