package com.peakform.security.forgotpassword.controller;

import com.peakform.security.forgotpassword.dto.ForgotPasswordRequest;
import com.peakform.security.forgotpassword.dto.ResetPasswordRequest;
import com.peakform.security.forgotpassword.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PasswordResetControllerImpl implements PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Override
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestPasswordReset(request);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}