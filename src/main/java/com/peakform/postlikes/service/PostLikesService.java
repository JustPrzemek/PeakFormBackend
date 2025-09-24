package com.peakform.postlikes.service;

import com.peakform.postlikes.dto.PostLikeResponseDTO;

public interface PostLikesService {
    PostLikeResponseDTO addLike(Long postId);

    PostLikeResponseDTO removeLike(Long postId);
}
