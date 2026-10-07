package com.codexhotel.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailNotificationService implements NotificationService {

    @Override
    public void notify(String receiver, String message) {
        log.info("Email notification sent to {}: {}", receiver, message);
    }
}
