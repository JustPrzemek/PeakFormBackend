package com.peakform.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddCommentDTO {
    @NotBlank(message = "Komentarz nie może być pusty.")
    @Size(max = 500, message = "Komentarz nie może być dłuższy niż 500 znaków.")
    private String content;
    private Long postId;
}
