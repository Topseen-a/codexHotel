package com.codexhotel.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * A single-use password reset token. Only a SHA-256 hash of the token is
 * stored, so a database leak can't be used to reset passwords. MongoDB's TTL
 * index removes the document once {@code expiresAt} passes.
 */
@Data
@Document(collection = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    private String id;

    private String userId;

    @Indexed(unique = true)
    private String tokenHash;

    @Indexed(expireAfter = "0s")
    private Instant expiresAt;

    private Instant createdAt;
}
