package com.peakform.userprofile.controller;

import com.peakform.userprofile.dto.EditUserDataDTO;
import com.peakform.userprofile.dto.UserProfileDTO;
import com.peakform.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserProfileControllerImpl implements UserProfileController {

    private final UserProfileService userProfileService;

    @Override
    public ResponseEntity<UserProfileDTO> getUserProfile(String username) {
        return ResponseEntity.ok(userProfileService.getUserProfile(username));
    }

    @Override
    public ResponseEntity<UserProfileDTO> getUserMe() {
        return ResponseEntity.ok(userProfileService.getUserMe());
    }

    @Override
    public ResponseEntity<EditUserDataDTO> updateUserData(EditUserDataDTO editUserDataDTO) {
        return ResponseEntity.ok(userProfileService.updateUserData(editUserDataDTO));
    }

    @Override
    public ResponseEntity<String> updateUserImage(MultipartFile file) {
        return ResponseEntity.ok(userProfileService.updateProfileImage(file));
    }

    @Override
    public ResponseEntity<EditUserDataDTO> getEditData() {
        return ResponseEntity.ok(userProfileService.getEditedUserData());
    }

    @Override
    public ResponseEntity<Void> updateFollowers(String username) {
        userProfileService.updateFollowers(username);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> unfollowUser(String username) {
        userProfileService.unfollowUser(username);
        return ResponseEntity.noContent().build();
    }
}
