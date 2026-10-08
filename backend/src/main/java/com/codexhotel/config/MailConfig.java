package com.codexhotel.config;

import com.codexhotel.config.properties.MailProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

import java.util.Properties;

/** Creates an SMTP mail sender only when MAIL_HOST is configured. */
@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnExpression("!'${app.mail.host:}'.isBlank()")
    public JavaMailSender javaMailSender(MailProperties properties) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(properties.host());
        sender.setPort(properties.port());
        if (StringUtils.hasText(properties.username())) {
            sender.setUsername(properties.username());
            sender.setPassword(properties.password());
        }

        Properties javaMail = sender.getJavaMailProperties();
        javaMail.put("mail.transport.protocol", "smtp");
        javaMail.put("mail.smtp.auth", String.valueOf(StringUtils.hasText(properties.username())));
        javaMail.put("mail.smtp.starttls.enable", "true");
        javaMail.put("mail.smtp.connectiontimeout", "10000");
        javaMail.put("mail.smtp.timeout", "10000");
        return sender;
    }
}
