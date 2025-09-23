package com.peakform.posts.dto;

import com.peakform.comments.dto.InsideImageCommentDTO;
import com.peakform.pages.PagedResponse;
import com.peakform.posts.enumerate.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostDetailsDTO {
    private Long postId;
    private String username;
    private String userProfileImageUrl;
    private String mediaUrl;
    private MediaType mediaType;
    private String content;
    private Long likesCount;
    private Long commentsCount;
    private LocalDateTime createdAt;
    private PagedResponse<InsideImageCommentDTO> comments;
}
