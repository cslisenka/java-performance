package com.example.agent.examples;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class ClockVsCPUTime {

    public static void main(String[] args) throws InterruptedException {
        ThreadMXBean mbean = ManagementFactory.getThreadMXBean();

        long startClockTime = System.nanoTime();
        long startCPUTime = mbean.getThreadCpuTime(Thread.currentThread().getId());

        // Delay sleep
        Thread.sleep(1000);

        // Delay loop
        long loopEndTime = System.currentTimeMillis() + 1000;
        while (System.currentTimeMillis() < loopEndTime) {
            // Nothing to do
        }

        System.out.println("Clock time: " + (System.nanoTime() - startClockTime) / 1000_000 + " ms");
        System.out.println("CPU time: " + (mbean.getThreadCpuTime(Thread.currentThread().getId()) - startCPUTime) / 1000_000 + " ms");
    }
}