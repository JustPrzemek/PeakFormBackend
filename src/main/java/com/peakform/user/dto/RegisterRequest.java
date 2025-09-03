package com.peakform.user.dto;

import lombok.Data;

@Data
public class  RegisterRequest {
    private String email;
    private String username;
    private String password;
    private Integer age;
}
