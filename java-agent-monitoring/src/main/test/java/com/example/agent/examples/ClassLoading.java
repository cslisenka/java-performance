package com.example.agent.examples;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

public class ClassLoading {

    public static void main(String[] args) {
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        System.out.println("Loaded classes: " + classLoadingMXBean.getLoadedClassCount());
        System.out.println("Total loaded classes: " + classLoadingMXBean.getTotalLoadedClassCount());
        System.out.println("Unloaded classes: " + classLoadingMXBean.getUnloadedClassCount());
    }
}