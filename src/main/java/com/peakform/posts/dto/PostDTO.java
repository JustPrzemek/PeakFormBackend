package com.peakform.posts.dto;

import com.peakform.posts.enumerate.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostDTO {
    private Long id;
    private String content;
    private String mediaUrl;
    private MediaType mediaType;
    private LocalDateTime createdAt;
    private Long likesCount;
    private Long commentsCount;
}
