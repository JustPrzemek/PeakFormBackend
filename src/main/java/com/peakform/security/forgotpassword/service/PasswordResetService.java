package com.peakform.security.forgotpassword.service;

import com.peakform.security.forgotpassword.dto.ForgotPasswordRequest;
import com.peakform.security.forgotpassword.dto.ResetPasswordRequest;

public interface PasswordResetService {

    void requestPasswordReset(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
