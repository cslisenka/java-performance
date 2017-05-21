package com.example.agent.examples;

import com.sun.management.ThreadMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.Arrays;

public class Threads {

    public static void main(String[] args) {
        System.out.println("Threads");
        ThreadMXBean threadMBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();

        System.out.println("Thread count = " + threadMBean.getThreadCount());
        System.out.println("Thread IDs = " + Arrays.toString(threadMBean.getAllThreadIds()));

        System.out.println(">>>>> Thread INFO");
        ThreadInfo[] threads = threadMBean.dumpAllThreads(true, true);
        for (ThreadInfo thread : threads) {
            System.out.println(thread);
        }

        System.out.println(">>>>> Thread INFO detailed");
        for (long threadId : threadMBean.getAllThreadIds()) {
            ThreadInfo threadInfo = threadMBean.getThreadInfo(threadId, 100);
            long cpuTime = threadMBean.getThreadCpuTime(threadId);
            long userTime = threadMBean.getThreadUserTime(threadId);
            long allocatedBytes = threadMBean.getThreadAllocatedBytes(threadId);
            System.out.println(threadInfo);
            System.out.println("CPU Time: " + cpuTime + ", USER time: " + userTime + ", Allocated bytes: " + allocatedBytes);
        }

        // Getting stack-traces of the current thread
        System.out.print(">> current thread: " + Thread.currentThread().getName() + "\n");
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            System.out.println(element);
        }
    }
}