package com.peakform.userprofile.controller;

import com.peakform.userprofile.dto.EditUserDataDTO;
import com.peakform.userprofile.dto.UserProfileDTO;
import com.peakform.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserProfileControllerImpl implements UserProfileController {

    private final UserProfileService userProfileService;

    @Value("${https.secure}")
    private Boolean isSecure;

    // Metoda pomocnicza (skopiuj z AuthController lub wydziel do utilsa)
    private ResponseCookie createRefreshTokenCookie(String token) {
        String sameSite = isSecure ? "None" : "Lax";
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(isSecure)
                .path("/api/auth/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite(sameSite)
                .build();
    }

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
        EditUserDataDTO updatedProfile = userProfileService.updateUserData(editUserDataDTO);

        if (updatedProfile.getNewRefreshToken() != null) {

            ResponseCookie cookie = createRefreshTokenCookie(updatedProfile.getNewRefreshToken());

             return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(updatedProfile);
        }
        return ResponseEntity.ok(updatedProfile);
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
