package com.peakform.postlikes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostLikeResponseDTO {
    private boolean isLikedByUser;
}
