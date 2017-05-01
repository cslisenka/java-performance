package com.frontent;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class LoadTest {

    RestTemplate template = new RestTemplate();
    int callsPerMinute = 60;

    @Test
    public void callChatSync() throws InterruptedException {
        while (true) {
            try {
                ChatResponse[] response = template.getForEntity("http://localhost:8989/chat?name={name}&message={message}",
                        ChatResponse[].class, "111", "222").getBody();

                System.out.println("response returned " + response.length + " entities");
                Thread.sleep(1000 * 60 / callsPerMinute);
            } catch (Exception e) {
                e.printStackTrace();
            }

            doSomething();
        }
    }

    static class ChatResponse {
        public String id;
        public String name;
        public String message;
        public String timestamp;

        @Override
        public String toString() {
            return "ChatResponse{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", message='" + message + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    "}\n";
        }
    }


    public void doSomething() {
        long start = System.currentTimeMillis();

        // Your method body goes here

        long time = System.currentTimeMillis() - start;
        System.out.println("Execution time = " + time);
    }
}