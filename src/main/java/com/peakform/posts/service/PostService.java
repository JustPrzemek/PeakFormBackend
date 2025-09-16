package com.peakform.posts.service;

import com.peakform.pages.PagedResponse;
import com.peakform.posts.dto.PostDTO;

public interface PostService {
    PagedResponse<PostDTO> getMyPosts(int page, int size);

    PagedResponse<PostDTO> getFeed(int page, int size);

    PagedResponse<PostDTO> getUserPosts(String username, int page, int size);
}
