package com.peakform.security.user.controller;

import com.peakform.pages.PagedResponse;
import com.peakform.security.user.dto.ProfilePhotoDTO;
import com.peakform.security.user.dto.UserSearchDTO;
import com.peakform.trainings.workoutplans.dto.SetActivePlanRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/userProfile")
@Tag(name = "User Profile", description = "User Profile")
public interface UserController {

    @GetMapping("/myProfilePhoto")
    @Operation(
            summary = "",
            description = ""
    )
    ResponseEntity<ProfilePhotoDTO> getMyProfilePhoto();

    @GetMapping("/search")
    ResponseEntity<PagedResponse<UserSearchDTO>> searchUsers(
            @RequestParam(name = "query", required = false, defaultValue = "") String query,
            @PageableDefault(size = 10, sort = "username") Pageable pageable);

    @PostMapping("/me/active-plan")
    @Operation(summary = "Ustawia aktywny plan treningowy dla użytkownika")
    ResponseEntity<Void> setActivePlan(@RequestBody SetActivePlanRequestDto requestDto);
}
