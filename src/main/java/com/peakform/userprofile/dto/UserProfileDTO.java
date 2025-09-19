package com.peakform.userprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileDTO {
    private String username;
    private String profileImageUrl;
    private String profileBio;
    private String bioTitle;
    private String location;
    private Long followersCount;
    private Long followingCount;
    private Long postsCount;
}
