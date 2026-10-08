package com.codexhotel.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/** SMTP settings. Email is only sent when {@code host} is set; otherwise it's logged. */
@ConfigurationProperties(prefix = "app.mail")
public record MailProperties(
        String host,
        int port,
        String username,
        String password,
        String from
) {

    public boolean enabled() {
        return StringUtils.hasText(host);
    }
}
