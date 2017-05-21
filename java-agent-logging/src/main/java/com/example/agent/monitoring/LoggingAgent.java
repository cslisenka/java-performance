package com.example.agent.monitoring;

import java.lang.instrument.Instrumentation;

public class LoggingAgent {

    public static void premain(String args, Instrumentation instr){
        System.out.format("[%s] [monitoring agent]\n", Thread.currentThread().getName());
        instr.addTransformer(new ClassLoadingLogger());
    }
}


