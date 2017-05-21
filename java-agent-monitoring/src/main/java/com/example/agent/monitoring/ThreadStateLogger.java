package com.example.agent.monitoring;

import com.sun.management.ThreadMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadStateLogger extends Thread {

    private final int logsPerMinute;
    private ConcurrentHashMap<Long, ThreadPrevValues> threadInfoMap = new ConcurrentHashMap<>();

    public ThreadStateLogger(int samplesPerMinute) {
        setName("thread-logger");
        setDaemon(true);
        this.logsPerMinute = samplesPerMinute;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            ThreadMXBean threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
            for (long threadId : threadMXBean.getAllThreadIds()) {
                ThreadInfo info = threadMXBean.getThreadInfo(threadId);
                ThreadPrevValues prevValues = getPrevValues(threadId);
                long cpuTime = threadMXBean.getThreadCpuTime(threadId);
                long allocatedBytes = threadMXBean.getThreadAllocatedBytes(threadId);

                System.out.println(info.getThreadName() +
                    " CPU time: " + prevValues.getDeltaCPUTime(cpuTime)  +
                    " Allocated bytes: " + prevValues.getDeltaBytes(allocatedBytes));

                prevValues.update(cpuTime, allocatedBytes);
            }

            delaySleep();
        }

        System.out.println("ThreadStateLogger stopped");
    }

    private void delaySleep() {
        try {
            Thread.sleep(1024 * 60 / logsPerMinute);
        } catch (InterruptedException e) {
        }
    }

    private ThreadPrevValues getPrevValues(long threadId) {
        ThreadPrevValues info = threadInfoMap.get(threadId);
        if (info == null) {
            info = new ThreadPrevValues();
            threadInfoMap.put(threadId, info);
        }

        return info;
    }

    static class ThreadPrevValues {
        private long prevCPUTime;
        private long prevBytes;

        public void update(long bytes, long cpuTime) {
            this.prevBytes = bytes;
            this.prevCPUTime = cpuTime;
        }

        public String getDeltaCPUTime(long cpuTime) {
            long result = cpuTime - prevCPUTime;
            return (result > 0 ? "+" : "") + result / 1000_000;
        }

        public String getDeltaBytes(long bytes) {
            long result = bytes - prevBytes;
            return (result > 0 ? "+" : "") + result;
        }
    }
}