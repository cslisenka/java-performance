package com.example.agent;

import java.lang.instrument.Instrumentation;

// TODO manipulate methods of class String, or Thread! Add this to the example
// TODO add custom classloader
public class Agent {

    public static void premain(String args, Instrumentation instrumentation){
        System.out.println("I am java agent " + args);
        String[] params = args.split("=");
        if (params.length == 3) {
            ClassTransformer transformer = new ClassTransformer(params[0], params[1], params[2]);
            instrumentation.addTransformer(transformer);
        } else {
            System.out.println("Wrong input parameters!");
        }
    }
}