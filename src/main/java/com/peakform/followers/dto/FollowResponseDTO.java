package com.peakform.followers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowResponseDTO {
    private long followersCount;
    private boolean isFollowing;
}
