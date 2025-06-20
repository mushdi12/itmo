package com.example.backend.points.mbean;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class PointsCounter extends NotificationBroadcasterSupport implements PointsCounterMBean {
    private int totalPoints = 0;
    private int inAreaPoints = 0;
    private int consecutiveMisses = 0;
    private long sequenceNumber = 1;

    public void registerPoint(boolean isInArea) {
        totalPoints++;
        if (isInArea) {
            inAreaPoints++;
            consecutiveMisses = 0;
        } else {
            consecutiveMisses++;
            if (consecutiveMisses == 3) {
                Notification notification = new Notification(
                        "three.consecutive.misses",
                        this,
                        sequenceNumber++,
                        System.currentTimeMillis(),
                        "User made 3 misses in a row"
                );
                sendNotification(notification);
                consecutiveMisses = 0;
            }
        }
    }

    @Override
    public int getTotalPoints() {
        return totalPoints;
    }

    @Override
    public int getPointsInArea() {
        return inAreaPoints;
    }
}
