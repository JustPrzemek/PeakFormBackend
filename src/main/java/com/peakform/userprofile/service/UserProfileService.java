package com.peakform.userprofile.service;

import com.peakform.userprofile.dto.EditUserDataDTO;
import com.peakform.userprofile.dto.UserProfileDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {

    UserProfileDTO getUserProfile(String username);

    UserProfileDTO getUserMe();

    EditUserDataDTO updateUserData(EditUserDataDTO dto);

    String updateProfileImage(MultipartFile file);

    EditUserDataDTO getEditedUserData();

    void updateFollowers(String username);

    void unfollowUser(String username);
}
