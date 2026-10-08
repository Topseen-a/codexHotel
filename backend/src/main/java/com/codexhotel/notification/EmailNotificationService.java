package com.codexhotel.notification;

import com.codexhotel.config.properties.MailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Sends email over SMTP when MAIL_HOST is configured; otherwise logs it, so
 * local development works without a mail server. Sending is asynchronous so
 * a slow SMTP server never delays an API response.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {

    private static final String DEFAULT_SUBJECT = "Your CodexHotel stay";

    private final ObjectProvider<JavaMailSender> mailSender;
    private final MailProperties mailProperties;

    @Async
    @Override
    public void notify(String receiver, String message) {
        deliver(receiver, DEFAULT_SUBJECT, message);
    }

    @Async
    public void send(String to, String subject, String body) {
        deliver(to, subject, body);
    }

    private void deliver(String to, String subject, String body) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || !mailProperties.enabled()) {
            log.info("Email (not sent — MAIL_HOST not configured) to {} | {} | {}", to, subject, body);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.from());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        try {
            sender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (MailException ex) {
            log.error("Failed to send email to {}: {}", to, ex.getMessage());
        }
    }
}
