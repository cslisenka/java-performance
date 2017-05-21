package com.example.agent.monitoring;

import com.sun.management.ThreadMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.Arrays;

public class ThreadSampler extends Thread {

    private final int samplesPerMinute;

    public ThreadSampler(int samplesPerMinute) {
        setName("sampler");
        setDaemon(true);
        this.samplesPerMinute = samplesPerMinute;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            ThreadMXBean threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
            System.out.println(">>>>>>>>>>> Dumping threads");
            ThreadInfo[] info = threadMXBean.dumpAllThreads(true, true);
            System.out.println(Arrays.toString(info));

            delaySleep();
        }

        System.out.println("ThreadSampler stopped");
    }

    private void delaySleep() {
        try {
            Thread.sleep(1024 * 60 / samplesPerMinute);
        } catch (InterruptedException e) {
        }
    }
}