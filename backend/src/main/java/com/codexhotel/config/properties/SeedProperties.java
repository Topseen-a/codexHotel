package com.codexhotel.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.seed")
public record SeedProperties(
        @NotBlank String adminEmail,
        @NotBlank String adminPassword
) {
}
