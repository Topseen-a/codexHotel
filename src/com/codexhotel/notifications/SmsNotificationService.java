package com.codexhotel.notifications;

import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService implements NotificationService {

    @Override
    public void notify(String receiver, String message) {
        System.out.println("SMS Notification sent to  " + receiver + ": " + message);
    }
}
