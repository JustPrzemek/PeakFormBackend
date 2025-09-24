package com.peakform.postlikes.controller;

import com.peakform.postlikes.dto.PostLikeResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/likes")
@Tag(name = "Likes", description = "Likes")
public interface PostLikesController {

    @PostMapping("/{postId}/like")
    @Operation(summary = "Add a like to a post")
    ResponseEntity<PostLikeResponseDTO> addLike(@PathVariable Long postId);

    @DeleteMapping("/{postId}/unlike")
    @Operation(summary = "Remove a like from a post")
    ResponseEntity<PostLikeResponseDTO> removeLike(@PathVariable Long postId);
}
