package com.example.agent.instrumenting;

import javassist.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class ClassTransformer implements ClassFileTransformer {

    private final String clazzName;
    private final String method;
    private final String saveTo;

    public ClassTransformer(String clazzName, String method, String saveTo) {
        this.clazzName = clazzName;
        this.method = method;
        this.saveTo = saveTo;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.replace("/", ".").equals(clazzName)) {
            System.out.println("Agent: instrumenting " + className);
            try {
                ClassPool classPool = ClassPool.getDefault();
                CtClass clazz = classPool.get(clazzName);
                addTiming(clazz, method);

                byte[] byteCode = clazz.toBytecode();
                clazz.detach();

                saveByteCode(byteCode, clazz.getSimpleName());

                System.out.println("Agent: instrumented successfully " + clazzName + "." + method);
                return byteCode;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return classfileBuffer;
    }

    private void addTiming(CtClass clazz, String methodName) throws NotFoundException, CannotCompileException {
        CtMethod oldMethod = clazz.getDeclaredMethod(methodName);

        // Rename old method to synthetic name
        String changedName = methodName + "$original";
        oldMethod.setName(changedName);

        // Duplicate the method with original name for use as interceptor
        CtMethod newMethod = CtNewMethod.copy(oldMethod, methodName, clazz, null);
        String type = oldMethod.getReturnType().getName();
        String code = String.format(
            "{" +
            "  long start = System.currentTimeMillis();" +
            "  %s result = %s($$);" +
            "  System.out.println(\"Agent: %s took \" + (System.currentTimeMillis() - start) + \" ms\");" +
            "  return result;" +
            "}", type, changedName, methodName);

        String voidCode = String.format(
            "{" +
            "  long start = System.currentTimeMillis();" +
            "  %s($$);" +
            "  System.out.println(\"Agent: %s took \" + (System.currentTimeMillis() - start) + \" ms\");" +
            "}", changedName, methodName);

        newMethod.setBody("void".equals(type) ? voidCode : code);
        clazz.addMethod(newMethod);
    }

    private void saveByteCode(byte[] byteCode, String className) throws IOException {
        String path = saveTo + "/" + className + ".class";
        FileOutputStream os = new FileOutputStream(path);
        os.write(byteCode);
        os.close();
        System.out.println("Agent: Saved " + path);
    }
}