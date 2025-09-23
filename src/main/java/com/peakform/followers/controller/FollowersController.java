package com.peakform.followers.controller;

import com.peakform.followers.dto.FollowDTO;
import com.peakform.followers.dto.FollowResponseDTO;
import com.peakform.pages.PagedResponse;
import com.peakform.postlikes.dto.PostLikeResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("/api/users")
@Tag(name = "Followers", description = "Followers")
public interface FollowersController {

    @GetMapping("/my/followers")
    @Operation(
            summary = "Get followers for user",
            description = "Returns a list of followers with optional filtering by username"
    )
    ResponseEntity<PagedResponse<FollowDTO>> getMyFollowers(
            @PageableDefault(page = 0, size = 20, sort = "follower.username", direction = Sort.Direction.ASC)
            Pageable pageable,
            @RequestParam(required = false) String username);


    @GetMapping("/my/following")
    @Operation(
            summary = "Get following for current user",
            description = "Returns data blblblblbablabalbal opisac"
    )
    ResponseEntity<PagedResponse<FollowDTO>> getMyFollowing(
            @PageableDefault(page = 0, size = 20, sort = "followed.username", direction = Sort.Direction.ASC)
            Pageable pageable);


    @PostMapping("/{username}/follow")
    @Operation(summary = "Add a like to a post")
    ResponseEntity<Void> followUser(@PathVariable String username);

    @DeleteMapping("/{username}/follow")
    @Operation(summary = "Remove a like from a post")
    ResponseEntity<Void> unfollowUser(@PathVariable String username);
}
