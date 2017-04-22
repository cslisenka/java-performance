package com.example.agent;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String args, Instrumentation instrumentation){
        System.out.println("I am java agent");
//        ClassLogger transformer = new ClassLogger();
//        instrumentation.addTransformer(transformer);
    }
}