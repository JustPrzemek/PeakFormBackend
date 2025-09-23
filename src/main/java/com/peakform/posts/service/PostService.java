package com.peakform.posts.service;

import com.peakform.pages.PagedResponse;
import com.peakform.posts.dto.FollowersPostsDTO;
import com.peakform.posts.dto.PostDTO;
import com.peakform.posts.dto.PostDetailsDTO;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    PagedResponse<PostDTO> getMyPosts(int page, int size);

    PagedResponse<FollowersPostsDTO> getFeed(int page, int size);

    PagedResponse<PostDTO> getUserPosts(String username, int page, int size);

    String createPost(String content, MultipartFile file);

    PostDetailsDTO getPostDetails(Long postId, int page, int size);
}
