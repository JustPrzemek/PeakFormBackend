package com.peakform.posts.controller;

import com.peakform.pages.PagedResponse;
import com.peakform.posts.dto.PostDTO;
import com.peakform.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class PostControllerImpl implements PostController{

    private final PostService postService;

    @Override
    public ResponseEntity<PagedResponse<PostDTO>> getMyPosts(int page, int size) {
        return ResponseEntity.ok(postService.getMyPosts(page, size));
    }

    @Override
    public ResponseEntity<PagedResponse<PostDTO>> getFeed(int page, int size) {
        return ResponseEntity.ok(postService.getFeed(page, size));
    }

    @Override
    public ResponseEntity<PagedResponse<PostDTO>> getUserPosts(String username,
                                                               int page,
                                                               int size
    ) {
        return ResponseEntity.ok(postService.getUserPosts(username, page, size));
    }

    public ResponseEntity<String> createPost(
            String content,
            MultipartFile file) {
        return ResponseEntity.ok(postService.createPost(content, file));
    }
}
