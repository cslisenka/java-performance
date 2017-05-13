package org.example.agent.logging;

import sun.net.spi.nameservice.dns.DNSNameService;

import java.util.HashMap;

/**
 * Demonstrates which classloaders java agent is able to intercept.
 * Java agent intercepts classloaders starting from AppClassLoader (System)
 * Each time when
 */
public class AgentWithClassLoaderTest {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        System.out.println("I am test application");

        System.out.println("Classloader for HashMap " + HashMap.class.getClassLoader()); // Bootstrap (rt.jar)
        System.out.println("Classloader for DNSNameService " + DNSNameService.class.getClassLoader()); // Extension (lib/ext)
        System.out.println("Classloader for this " + AgentWithClassLoaderTest.class.getClassLoader()); // System (classpath)

        Class self = AgentWithClassLoaderTest.class.getClassLoader().loadClass("org.example.agent.logging.AgentWithClassLoaderTest");

        System.out.println("Before custom classloader");
        CustomClassLoader myClassLoader = new CustomClassLoader(AgentWithClassLoaderTest.class.getClassLoader());
        Class self2 = myClassLoader.loadClass("org.example.agent.logging.AgentWithClassLoaderTest");
        System.out.println("Create custom classloader secomd time");
        Class self3 = new CustomClassLoader(AgentWithClassLoaderTest.class.getClassLoader()).loadClass("org.example.agent.logging.AgentWithClassLoaderTest");

        System.out.format("%s %s\n", self.getName(), self.getClassLoader().getClass().getName());
        System.out.format("%s %s\n", self2.getName(), self2.getClassLoader().getClass().getName());
        System.out.format("%s %s\n", self3.getName(), self3.getClassLoader().getClass().getName());
    }
}