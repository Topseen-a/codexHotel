package com.codexhotel.service.impl;

import com.codexhotel.config.properties.PasswordResetProperties;
import com.codexhotel.dto.request.ForgotPasswordRequest;
import com.codexhotel.dto.request.ResetPasswordRequest;
import com.codexhotel.exception.BadRequestException;
import com.codexhotel.model.PasswordResetToken;
import com.codexhotel.model.User;
import com.codexhotel.notification.EmailNotificationService;
import com.codexhotel.repository.PasswordResetTokenRepository;
import com.codexhotel.repository.UserRepository;
import com.codexhotel.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final String INVALID_LINK = "This reset link is invalid or has expired. Please request a new one.";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailNotificationService emailService;
    private final PasswordResetProperties properties;

    @Override
    public void requestReset(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail().trim()).ifPresentOrElse(
                user -> {
                    String token = issueToken(user);
                    String link = properties.frontendUrl().replaceAll("/+$", "") + "/reset-password?token=" + token;
                    emailService.send(user.getEmail(), "Reset your CodexHotel password", """
                            Hi %s,

                            We received a request to reset your CodexHotel password. Use the link below to choose a new one:

                            %s

                            This link expires in %d minutes and can only be used once. If you didn't ask for this, you can ignore this email — your password won't change.
                            """.formatted(user.getName(), link, properties.tokenTtlMinutes()));
                },
                () -> log.debug("Password reset requested for an unknown email")
        );
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByTokenHash(hash(request.getToken()))
                .filter(token -> token.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new BadRequestException(INVALID_LINK));

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new BadRequestException(INVALID_LINK));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);

        // Single use: drop this and any other outstanding links for the account.
        tokenRepository.deleteByUserId(user.getId());

        emailService.send(user.getEmail(), "Your CodexHotel password was changed", """
                Hi %s,

                Your CodexHotel password was just changed and you've been signed out of other devices.

                If this wasn't you, reset your password again straight away and contact us.
                """.formatted(user.getName()));
    }

    /** Creates a fresh token for the user (replacing earlier ones) and returns the raw value. */
    String issueToken(User user) {
        tokenRepository.deleteByUserId(user.getId());

        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUserId(user.getId());
        resetToken.setTokenHash(hash(token));
        resetToken.setCreatedAt(Instant.now());
        resetToken.setExpiresAt(Instant.now().plus(properties.tokenTtlMinutes(), ChronoUnit.MINUTES));
        tokenRepository.save(resetToken);
        return token;
    }

    static String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}
