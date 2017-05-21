package com.example.agent.examples;

import com.example.agent.monitoring.GCLogger;
import com.sun.management.GarbageCollectorMXBean;

import javax.management.NotificationEmitter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Arrays;

public class MemoryAndGC {

    public static void main(String[] args) throws InterruptedException {
        System.out.println(">>>>> Memory");
        MemoryMXBean memoryMBean = ManagementFactory.getMemoryMXBean();
        System.out.println("Heap memory usage: " + memoryMBean.getHeapMemoryUsage());
        System.out.println("Non-Heap memory usage: " + memoryMBean.getNonHeapMemoryUsage());
        System.out.println("Objects pending finalization: " + memoryMBean.getObjectPendingFinalizationCount());

        System.out.println(">>>>> GC");
        ManagementFactory.getGarbageCollectorMXBeans().stream().forEach(bean -> {
            GarbageCollectorMXBean gcBean = (GarbageCollectorMXBean) bean;
            ((NotificationEmitter) gcBean).addNotificationListener(new GCLogger(), null, null);

            System.out.println(">> " + gcBean.getName());
            System.out.println("Collection count: " + gcBean.getCollectionCount());
            System.out.println("Collection time: " + gcBean.getCollectionTime());
            System.out.println("Last GC: " + gcBean.getLastGcInfo());
        });

        System.out.println(">>>>> Memory pool mbeans");
        ManagementFactory.getMemoryPoolMXBeans().stream().forEach(bean -> {
            System.out.println(">> " + bean.getName() + " " + bean.getType());
            System.out.println("Collection usage: " + bean.getCollectionUsage());
            System.out.println("Peak usage: " + bean.getPeakUsage());
            System.out.println("Usage: " + bean.getUsage());
            System.out.println("Memory manager names: " + Arrays.toString(bean.getMemoryManagerNames()));
        });


        while (true) {
            System.out.println("Allocating 1Mb");
            byte[] allocation = new byte[1024 * 1024];
            Thread.sleep(100);
        }
    }
}