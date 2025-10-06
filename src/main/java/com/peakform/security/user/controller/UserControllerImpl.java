package com.peakform.security.user.controller;

import com.peakform.pages.PagedResponse;
import com.peakform.security.user.dto.ProfilePhotoDTO;
import com.peakform.security.user.dto.UserSearchDTO;
import com.peakform.security.user.service.UserService;
import com.peakform.trainings.workoutplans.dto.SetActivePlanRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ResponseEntity<ProfilePhotoDTO> getMyProfilePhoto() {
        return ResponseEntity.ok(userService.getMyProfilePhoto());
    }

    @Override
    public ResponseEntity<PagedResponse<UserSearchDTO>> searchUsers(String query, Pageable pageable) {
        PagedResponse<UserSearchDTO> results = userService.searchUsers(query, pageable);
        return ResponseEntity.ok(results);
    }

    @Override
    public ResponseEntity<Void> setActivePlan(SetActivePlanRequestDto requestDto) {
        userService.setActivePlan(requestDto.getPlanId());
        return ResponseEntity.ok().build();
    }
}
