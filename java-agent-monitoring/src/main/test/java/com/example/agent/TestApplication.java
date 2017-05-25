package com.example.agent;

import com.example.agent.monitoring.GCLogger;
import com.example.agent.monitoring.ThreadSampler;
import com.example.agent.monitoring.ThreadStateLogger;

public class TestApplication {

    public static void main(String[] args) throws InterruptedException {
//        new GCLogger();
//        new ThreadSampler(60).start();
//        new ThreadStateLogger(6).start();

        while (true) {
            byte[] allocated = new byte[1024 * 1024];
            Thread.sleep(100);
        }
    }
}