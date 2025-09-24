package com.peakform.comments.service;

import com.peakform.comments.dto.AddCommentDTO;
import com.peakform.comments.dto.CommentsDTO;
import com.peakform.comments.dto.ResponseCommentDTO;
import com.peakform.comments.model.Comments;
import com.peakform.pages.PagedResponse;

public interface CommentsService {
    PagedResponse<CommentsDTO> getCommentsForPost(int page, int size, Long postID);

    ResponseCommentDTO addCommentForPost(AddCommentDTO addCommentDTO);
}
