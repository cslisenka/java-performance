package com.example.agent.diagnostic;

import com.sun.management.GarbageCollectorMXBean;

import javax.management.NotificationEmitter;
import java.lang.management.ManagementFactory;

public class DiagnosticThread extends Thread {

    public DiagnosticThread() {
        setName("diagnostic-thread");
        setDaemon(true);

        // Add GC notification listener
        ManagementFactory.getGarbageCollectorMXBeans().stream().forEach(bean -> {
            GarbageCollectorMXBean gcBean = (GarbageCollectorMXBean) bean;
            ((NotificationEmitter) gcBean).addNotificationListener(new GCNotificationListener(), null, null);
        });
    }

    @Override
    public void run() {

    }
}