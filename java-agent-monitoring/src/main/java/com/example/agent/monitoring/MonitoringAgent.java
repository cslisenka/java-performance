package com.example.agent.monitoring;

import java.lang.instrument.Instrumentation;

public class MonitoringAgent {

    public static void premain(String args, Instrumentation instr){
        System.out.println("Starting monitoring agent");

        new GCLogger();
        new ThreadStateLogger(6).start();
        new ThreadSampler(3).start();
    }
}