package com.peakform.user.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
