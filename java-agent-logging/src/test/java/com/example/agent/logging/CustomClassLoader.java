package com.example.agent.logging;

import java.io.*;

public class CustomClassLoader extends ClassLoader {

    protected CustomClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class clazz = findLoadedClass(name);

        if (clazz != null) {
            return clazz;
        }

        if (name.contains(AgentWithClassLoaderTest.class.getName())) {
            byte[] bytes = read(new File("").getAbsolutePath() + "\\java-agent-monitoring\\src\\test\\resources", AgentWithClassLoaderTest.class.getSimpleName());
            clazz = defineClass(name, bytes, 0, bytes.length);
            resolveClass(clazz);
            return clazz;
        } else {
            return getParent().loadClass(name);
        }
    }

    private byte[] read(String classPathRoot, String fullyQualifiedClassName) {
        final String path = fullyQualifiedClassName.replace(".", "/") + ".class";
        final File file = new File(classPathRoot, path);
        try (final InputStream stream = new FileInputStream(file);
             final ByteArrayOutputStream bos =
                     new ByteArrayOutputStream((int)file.length());
        ) {
            int len;
            final byte[] buffer = new byte[1024];
            while ((len = stream.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error reading bytecode", e);
        }
    }
}