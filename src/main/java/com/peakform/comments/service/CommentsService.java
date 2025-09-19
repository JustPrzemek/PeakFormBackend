package com.peakform.comments.service;

import com.peakform.comments.dto.CommentsDTO;
import com.peakform.pages.PagedResponse;

public interface CommentsService {
    PagedResponse<CommentsDTO> getCommentsForPost(int page, int size, Long postID);
}
