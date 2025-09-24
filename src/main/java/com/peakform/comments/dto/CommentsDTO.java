package com.peakform.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentsDTO {
    private Long commentId;
    private String content;
    private String username;
    private LocalDateTime createdAt;
}
