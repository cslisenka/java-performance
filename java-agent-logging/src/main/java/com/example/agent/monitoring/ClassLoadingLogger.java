package com.example.agent.monitoring;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class ClassLoadingLogger implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.format("[%s] [monitoring agent] ClassLoader: %s Class: %s\n",
                Thread.currentThread().getName(), loader.getClass().getName(), className);
        return classfileBuffer;
    }
}