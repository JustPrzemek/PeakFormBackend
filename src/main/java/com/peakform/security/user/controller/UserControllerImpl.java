package com.peakform.security.user.controller;

import com.peakform.security.user.dto.ProfilePhotoDTO;
import com.peakform.security.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ResponseEntity<ProfilePhotoDTO> getMyProfilePhoto() {
        return ResponseEntity.ok(userService.getMyProfilePhoto());
    }
}
