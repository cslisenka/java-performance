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
        String changedName = methodName + "$orig";
        oldMethod.setName(changedName);

        // Duplicate the method with original name for use as interceptor
        CtMethod newMethod = CtNewMethod.copy(oldMethod, methodName, clazz, null);

        String type = oldMethod.getReturnType().getName();

        StringBuffer body = new StringBuffer();
        body.append("{ long start = System.currentTimeMillis();");

        if (!"void".equals(type)) {
            body.append(type + " result = "); // Capture returned value (if not void)
        }

        body.append(changedName + "($$);"); // Call existed method

        //  Print timing information
        body.append("System.out.println(\"Agent: " + methodName + " took \" + (System.currentTimeMillis()-start) + \" ms.\");");

        if (!"void".equals(type)) {
            body.append("return result;"); //  Return captured value (if not void)
        }

        body.append("}");

        newMethod.setBody(body.toString());
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