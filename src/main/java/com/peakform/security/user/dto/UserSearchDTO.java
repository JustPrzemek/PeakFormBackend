package com.peakform.security.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSearchDTO {
    private Long id;
    private String username;
    private String profileImageUrl;
}
