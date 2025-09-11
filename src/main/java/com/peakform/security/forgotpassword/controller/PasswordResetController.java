package com.peakform.security.forgotpassword.controller;

import com.peakform.security.forgotpassword.dto.ForgotPasswordRequest;
import com.peakform.security.forgotpassword.dto.ResetPasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
public interface PasswordResetController {

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Verify email",
            description = "opisac jeszce takie stylistyczne rzeczy")
    ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request);

    @PostMapping("/reset-password")
    @Operation(
            summary = "Verify email",
            description = "opisac jeszce takie stylistyczne rzeczy")
    ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request);
}
