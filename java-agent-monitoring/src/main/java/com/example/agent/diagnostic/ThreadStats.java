package com.example.agent.diagnostic;

public class ThreadStats {

    private long lastCpuTime;
    private long lastUserTime;
    private long lastAllocatedBytes;

    public long getLastCpuTime() {
        return lastCpuTime;
    }

    public void setLastCpuTime(long lastCpuTime) {
        this.lastCpuTime = lastCpuTime;
    }

    public long getLastUserTime() {
        return lastUserTime;
    }

    public void setLastUserTime(long lastUserTime) {
        this.lastUserTime = lastUserTime;
    }

    public long getLastAllocatedBytes() {
        return lastAllocatedBytes;
    }

    public void setLastAllocatedBytes(long lastAllocatedBytes) {
        this.lastAllocatedBytes = lastAllocatedBytes;
    }
}