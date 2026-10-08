package com.codexhotel.service.impl;

import com.codexhotel.dto.request.ForgotPasswordRequest;
import com.codexhotel.dto.request.ResetPasswordRequest;
import com.codexhotel.enums.Role;
import com.codexhotel.exception.BadRequestException;
import com.codexhotel.model.PasswordResetToken;
import com.codexhotel.model.User;
import com.codexhotel.repository.PasswordResetTokenRepository;
import com.codexhotel.repository.UserRepository;
import com.codexhotel.security.JwtService;
import com.codexhotel.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PasswordResetServiceTest {

    @Autowired
    private PasswordResetServiceImpl passwordResetService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User user;

    @BeforeEach
    public void setUp() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("Oluwaseun");
        user.setEmail("oluwaseun@gmail.com");
        user.setPhoneNumber("08012345678");
        user.setPassword(passwordEncoder.encode("oldPassword1"));
        user.setRole(Role.GUEST);
        user.setCreatedAt(LocalDate.now());
        user = userRepository.save(user);
    }

    private ResetPasswordRequest resetRequest(String token, String newPassword) {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        request.setNewPassword(newPassword);
        return request;
    }

    @Test
    public void testThatRequestingResetForUnknownEmailSucceedsSilently() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("nobody@gmail.com");

        assertDoesNotThrow(() -> passwordResetService.requestReset(request));
        assertEquals(0, tokenRepository.count());
    }

    @Test
    public void testThatRequestingResetStoresOnlyA_hashOfTheToken() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("oluwaseun@gmail.com");

        passwordResetService.requestReset(request);

        assertEquals(1, tokenRepository.count());
        PasswordResetToken stored = tokenRepository.findAll().get(0);
        assertEquals(user.getId(), stored.getUserId());
        assertEquals(64, stored.getTokenHash().length());
        assertTrue(stored.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    public void testThatA_validTokenResetsThePasswordAndCannotBeReused() {
        String token = passwordResetService.issueToken(user);

        passwordResetService.resetPassword(resetRequest(token, "newPassword1"));

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("newPassword1", updated.getPassword()));
        assertNotNull(updated.getPasswordChangedAt());
        assertEquals(0, tokenRepository.count());
        assertThrows(BadRequestException.class, () -> passwordResetService.resetPassword(resetRequest(token, "another123")));
    }

    @Test
    public void testThatAnExpiredTokenIsRejected() {
        String token = passwordResetService.issueToken(user);
        PasswordResetToken stored = tokenRepository.findAll().get(0);
        stored.setExpiresAt(Instant.now().minus(1, ChronoUnit.MINUTES));
        tokenRepository.save(stored);

        assertThrows(BadRequestException.class, () -> passwordResetService.resetPassword(resetRequest(token, "newPassword1")));
    }

    @Test
    public void testThatAnUnknownTokenIsRejected() {
        assertThrows(BadRequestException.class,
                () -> passwordResetService.resetPassword(resetRequest("not-a-real-token", "newPassword1")));
    }

    @Test
    public void testThatRequestingA_newLinkInvalidatesTheOldOne() {
        String first = passwordResetService.issueToken(user);
        String second = passwordResetService.issueToken(user);

        assertThrows(BadRequestException.class, () -> passwordResetService.resetPassword(resetRequest(first, "newPassword1")));
        assertDoesNotThrow(() -> passwordResetService.resetPassword(resetRequest(second, "newPassword1")));
    }

    @Test
    public void testThatSessionsIssuedBeforeA_passwordChangeAreRejected() {
        String oldSession = jwtService.generateToken(new UserPrincipal(user));
        assertTrue(jwtService.isTokenValid(oldSession, new UserPrincipal(user)));

        user.setPasswordChangedAt(Instant.now().plus(2, ChronoUnit.SECONDS));
        User changed = userRepository.save(user);

        assertFalse(jwtService.isTokenValid(oldSession, new UserPrincipal(changed)));
    }
}
