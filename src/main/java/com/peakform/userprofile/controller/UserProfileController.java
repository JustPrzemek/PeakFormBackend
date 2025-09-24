package com.peakform.userprofile.controller;

import com.peakform.followers.dto.FollowDTO;
import com.peakform.userprofile.dto.EditUserDataDTO;
import com.peakform.userprofile.dto.UserProfileDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/userProfile")
@Tag(name = "User Profile", description = "User Profile")
public interface UserProfileController {

    @GetMapping("/{username}/profile")
    @Operation(
            summary = "Get someone else user profile",
            description = "Returns public profile data for given username"
    )
    ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable String username);


    @GetMapping("/me")
    @Operation(
            summary = "Get my profile",
            description = " Returns data about user in token"
    )
    ResponseEntity<UserProfileDTO> getUserMe();

    @PatchMapping("/updateProfileData")
    @Operation(
            summary = "Update current user's profile data",
            description = "Partially updates the profile data for the authenticated user."
    )
    ResponseEntity<EditUserDataDTO> updateUserData(@RequestBody EditUserDataDTO editUserDataDTO);

    @PostMapping(path = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "update profile image",
            description = ""
    )
    ResponseEntity<String> updateUserImage(
            @Parameter(description = "Profile image", required = true)
            @RequestPart("file") MultipartFile file);

    @GetMapping("/getEditData")
    @Operation(
            summary = "get data to be edited",
            description = ""
    )
    ResponseEntity<EditUserDataDTO> getEditData();

    @PostMapping("/follow")
    @Operation(
            summary = "update data about followers",
            description = ""
    )
    ResponseEntity<Void> updateFollowers(@RequestParam String username);

    @DeleteMapping("/unfollow")
    @Operation(
            summary = "update data about followers",
            description = ""
    )
    ResponseEntity<Void> unfollowUser(@RequestParam String username);

}
