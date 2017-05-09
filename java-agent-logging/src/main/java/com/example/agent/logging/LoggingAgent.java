package com.example.agent.logging;

import java.lang.instrument.Instrumentation;

public class LoggingAgent {

    public static void premain(String args, Instrumentation instr){
        instr.addTransformer(new ClassLoadingLogger());
    }
}


