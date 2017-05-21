package com.example.memory;

import javax.management.Notification;
import javax.management.NotificationListener;

public class GCNotificationListener implements NotificationListener {

    @Override
    public void handleNotification(Notification notification, Object handback) {
        System.out.println(notification);
        System.out.println(handback);
    }
}
