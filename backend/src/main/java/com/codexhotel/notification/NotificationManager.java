package com.codexhotel.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class NotificationManager {

    private final EmailNotificationService emailNotificationService;
    private final SmsNotificationService smsNotificationService;

    public void notifyByEmailAndSms(String email, String phoneNumber, String message) {
        if (StringUtils.hasText(email)) {
            emailNotificationService.notify(email, message);
        }
        if (StringUtils.hasText(phoneNumber)) {
            smsNotificationService.notify(phoneNumber, message);
        }
    }
}
