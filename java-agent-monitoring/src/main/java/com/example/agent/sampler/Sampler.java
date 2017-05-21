package com.example.agent.sampler;

import com.sun.management.ThreadMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;

public class Sampler extends Thread {

    private final int samplesPerMinute;

    public Sampler(int samplesPerMinute) {
        setName("sampler");
        setDaemon(true);
        this.samplesPerMinute = samplesPerMinute;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
//            delaySleep();
            delayLoop();

            // Get thread dumps
            ThreadMXBean threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
//            ThreadInfo[] infos = threadMXBean.dumpAllThreads(true, true);
//            System.out.println(">>>>>>>>>>> Dumping threads");
//            System.out.println(infos);

            for (long threadId : threadMXBean.getAllThreadIds()) {
                ThreadInfo info = threadMXBean.getThreadInfo(threadId);
                System.out.println(info.getThreadName() +
                        " CPU time: " + threadMXBean.getThreadCpuTime(threadId) +
                        " USER time: " + threadMXBean.getThreadUserTime(threadId));

            }
            System.out.println();
        }
    }

    private void delaySleep() {
        try {
            Thread.sleep(1024 * 60 / samplesPerMinute);
        } catch (InterruptedException e) {
        }
    }

    private void delayLoop() {
        long timeToSleep = 1024 * 60 / samplesPerMinute;
        long initialTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < initialTime + timeToSleep) {
            // Nothing to do
        }
    }
}