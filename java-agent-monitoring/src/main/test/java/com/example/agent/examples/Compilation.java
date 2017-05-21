package com.example.agent.examples;

import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;

public class Compilation {

    public static void main(String[] args) {
        CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
        System.out.println("Total compilation time: " + compilationMXBean.getTotalCompilationTime());
    }
}
