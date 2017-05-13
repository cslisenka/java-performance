package com.example.agent.instrumenting;

import java.lang.instrument.Instrumentation;

public class InstrumentingAgent {

    public static void premain(String args, Instrumentation instrumentation){
        // Agent intercepts class-loading for all class loaders starting from AppClassLoader (System)
        System.out.println("Agent: I am instrumenting java agent " + args);
        String[] params = args.split("=");
        if (params.length == 3) {
            String className = params[0];
            String method = params[1];
            String saveTo = params[2];

            ClassTransformer transformer = new ClassTransformer(className, method, saveTo);
            instrumentation.addTransformer(transformer);
        } else {
            System.out.println("Agent: Wrong input parameters! Agent could not modify byte code.");
        }
    }
}