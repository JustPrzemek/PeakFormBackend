package com.peakform.security.user.controller;

import com.peakform.security.user.dto.ProfilePhotoDTO;
import com.peakform.userprofile.dto.UserProfileDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/userProfile")
@Tag(name = "User Profile", description = "User Profile")
public interface UserController {

    @GetMapping("/myProfilePhoto")
    @Operation(
            summary = "",
            description = ""
    )
    ResponseEntity<ProfilePhotoDTO> getMyProfilePhoto();
}
