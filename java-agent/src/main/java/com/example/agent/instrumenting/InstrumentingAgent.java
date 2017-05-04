package com.example.agent.instrumenting;

import java.lang.instrument.Instrumentation;

// TODO manipulate methods of class String, or Thread! Add this to the example
// TODO add custom classloader
public class InstrumentingAgent {

    public static void premain(String args, Instrumentation instrumentation){
        System.out.println("Agent: I am instrumenting java agent " + args);
        String[] params = args.split("=");
        if (params.length == 3) {
            String clazz = params[0];
            String method = params[1];
            String saveByteCodeTo = params[2];

            System.out.println("Agent: Instrumented class: " + clazz);
            System.out.println("Agent: Instrumented method: " + method);
            System.out.println("Agent: Bytecode saved to: " + saveByteCodeTo);

            ClassTransformer transformer = new ClassTransformer(clazz, method, saveByteCodeTo);
            instrumentation.addTransformer(transformer);
        } else {
            System.out.println("Agent: Wrong input parameters! Agent could not modify byte code.");
        }
    }
}