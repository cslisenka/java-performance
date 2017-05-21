package com.example.agent.examples;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class OS {

    public static void main(String[] args) {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        System.out.println("Platform: " + operatingSystemMXBean.getArch());
        System.out.println("Name: " + operatingSystemMXBean.getName());
        System.out.println("Version: " + operatingSystemMXBean.getVersion());
        System.out.println("Available processors: " + operatingSystemMXBean.getAvailableProcessors());
        System.out.println("Average load: " + operatingSystemMXBean.getSystemLoadAverage());

        System.out.println("Committed virtual memory : " + operatingSystemMXBean.getCommittedVirtualMemorySize());

        System.out.println("Total physical memory: " + operatingSystemMXBean.getTotalPhysicalMemorySize());
        System.out.println("Free physical memory: " + operatingSystemMXBean.getFreePhysicalMemorySize());

        System.out.println("Total swap space: " + operatingSystemMXBean.getTotalSwapSpaceSize());
        System.out.println("Free swap space: " + operatingSystemMXBean.getFreeSwapSpaceSize());

        System.out.println("Process CPU load: " + operatingSystemMXBean.getProcessCpuLoad() + "%");
        System.out.println("Process CPU time: " + operatingSystemMXBean.getProcessCpuTime());
        System.out.println("System CPU load: " + operatingSystemMXBean.getSystemCpuLoad() + "%");
    }
}
