package com.codexhotel.service;

import com.codexhotel.dto.request.ForgotPasswordRequest;
import com.codexhotel.dto.request.ResetPasswordRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface PasswordResetService {

    /**
     * Emails a reset link if an account exists for the address. Always
     * completes silently, so callers can't tell which emails are registered.
     */
    void requestReset(@Valid ForgotPasswordRequest request);

    /** Sets a new password using a valid, unexpired, unused reset token. */
    void resetPassword(@Valid ResetPasswordRequest request);
}
