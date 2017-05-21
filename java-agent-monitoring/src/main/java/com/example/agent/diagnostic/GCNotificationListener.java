package com.example.agent.diagnostic;

import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.MemoryUsage;
import java.util.Map;

public class GCNotificationListener implements NotificationListener {

    private long totalGcDuration;

    @Override
    public void handleNotification(Notification notification, Object handback) {
        if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
            GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
            // Get all the info and pretty print it
            long duration = info.getGcInfo().getDuration();
            String gctype = info.getGcAction();
            if ("end of minor GC".equals(gctype)) {
                gctype = "Young Gen GC";
            } else if ("end of major GC".equals(gctype)) {
                gctype = "Old Gen GC";
            }
            System.out.println();
            System.out.println(gctype + ": - " + info.getGcInfo().getId()+ " " + info.getGcName() + " (from " + info.getGcCause()+") "+duration + " milliseconds; start-end times " + info.getGcInfo().getStartTime()+ "-" + info.getGcInfo().getEndTime());
            //System.out.println("GcInfo CompositeType: " + info.getGcInfo().getCompositeType());
            //System.out.println("GcInfo MemoryUsageAfterGc: " + info.getGcInfo().getMemoryUsageAfterGc());
            //System.out.println("GcInfo MemoryUsageBeforeGc: " + info.getGcInfo().getMemoryUsageBeforeGc());

            //Get the information about each memory space, and pretty print it
            Map<String, MemoryUsage> membefore = info.getGcInfo().getMemoryUsageBeforeGc();
            Map<String, MemoryUsage> mem = info.getGcInfo().getMemoryUsageAfterGc();
            for (Map.Entry<String, MemoryUsage> entry : mem.entrySet()) {
                String name = entry.getKey();
                MemoryUsage memdetail = entry.getValue();
                long memInit = memdetail.getInit();
                long memCommitted = memdetail.getCommitted();
                long memMax = memdetail.getMax();
                long memUsed = memdetail.getUsed();
                MemoryUsage before = membefore.get(name);
                long beforepercent = ((before.getUsed() * 1000L) / before.getCommitted());
                long percent = ((memUsed * 1000L) / before.getCommitted()); // >100% when it gets expanded

                System.out.println(name + (memCommitted == memMax ? "(fully expanded)" : "(still expandable)") +
                        "used: " + (beforepercent / 10) + "." + (beforepercent % 10) + "%->" + (percent / 10) + "." + (percent % 10) + "%(" + ((memUsed / 1048576) + 1) + "MB)");
            }

            System.out.println();

            totalGcDuration += info.getGcInfo().getDuration();
            long percent = totalGcDuration *1000L / info.getGcInfo().getEndTime();
            System.out.println("GC cumulated overhead " + (percent / 10) + "." + (percent%10) + "%");
        }
    }
}
