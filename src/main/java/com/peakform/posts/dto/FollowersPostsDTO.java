package com.peakform.posts.dto;

import com.peakform.comments.dto.CommentsDTO;
import com.peakform.posts.enumerate.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowersPostsDTO {
    private Long id;
    private String username;
    private String profileImageUrl;
    private String content;
    private String mediaUrl;
    private MediaType mediaType;
    private LocalDateTime createdAt;
    private Long likesCount;
    private Long commentsCount;
    private List<CommentsDTO> comments;

    public FollowersPostsDTO(Long id, String username, String profileImageUrl, String content, String mediaUrl, MediaType mediaType, LocalDateTime createdAt, Long likesCount, Long commentsCount) {
        this.id = id;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.createdAt = createdAt;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }
}
