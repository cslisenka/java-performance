package com.example.compilation;

import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;

public class MainCompilation {

    public static void main(String[] args) {
        CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
        System.out.println("Total compilation time: " + compilationMXBean.getTotalCompilationTime());
    }
}
