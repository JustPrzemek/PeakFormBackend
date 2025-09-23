package com.peakform.postlikes.controller;

import com.peakform.postlikes.dto.PostLikeResponseDTO;
import com.peakform.postlikes.service.PostLikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostLikesControllerImpl implements PostLikesController {

    private final PostLikesService postLikesService;

    @Override
    public ResponseEntity<PostLikeResponseDTO> addLike(Long postId) {
        PostLikeResponseDTO response = postLikesService.addLike(postId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PostLikeResponseDTO> removeLike(Long postId) {
        PostLikeResponseDTO response = postLikesService.removeLike(postId);
        return ResponseEntity.ok(response);
    }
}
