package com.example.agent.diagnostic;

import com.example.agent.sampler.Sampler;

public class Test {

    public static void main(String[] args) throws InterruptedException {
//        new DiagnosticThread().start();
        new Sampler(6).start();

        while (true) {
            byte[] allocated = new byte[1024 * 1024];
            Thread.sleep(100);
        }
    }
}