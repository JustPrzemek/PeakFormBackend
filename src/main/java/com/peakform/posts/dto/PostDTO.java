package com.peakform.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostDTO {
    private Long id;
    private String content;
    private String postImageUrl;
    private LocalDateTime createdAt;
    private Long likesCount;
    private Long commentsCount;
}
