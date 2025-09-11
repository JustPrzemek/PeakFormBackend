package com.peakform.security.user.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
