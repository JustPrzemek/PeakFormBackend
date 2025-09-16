package com.peakform.claudinary.controller;

import com.peakform.claudinary.service.ImageUploadService;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ImageController {

    private final UserRepository userRepository;
    private final ImageUploadService imageUploadService;

    @PostMapping(path = "/{username}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload profile image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable String username,
            @Parameter(description = "Profile image", required = true)
            @RequestPart("file") MultipartFile file) throws IOException {

        String imageUrl = imageUploadService.uploadImage(file);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);

        return ResponseEntity.ok(imageUrl);
    }
}
