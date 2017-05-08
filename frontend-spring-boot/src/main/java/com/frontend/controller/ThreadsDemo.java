package com.frontend.controller;

import com.frontend.dto.AsyncResponseDTO;
import com.frontend.dto.TextMessage;
import com.frontend.service.Backend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class ThreadsDemo {

    @Autowired
    private Backend backend;

    @Autowired
    @Qualifier("threadPool")
    private ExecutorService threadPool;

    @RequestMapping("/chatNewThread")
    public TextMessage[] chatNewThread(@RequestParam(value="name") String name,
                                       @RequestParam(value="message") String message) throws InterruptedException {
        AtomicReference<TextMessage[]> result = new AtomicReference<>();
        // Dynatrace does not associate new thread with current pure path
        Thread newThread = new Thread(() -> {
            result.set(backend.sendHTTP(name, message));
        });

        newThread.start();
        newThread.join();
        return result.get();
    }

    @RequestMapping("/chatThreadPool")
    public TextMessage[] chatThreadPool(@RequestParam(value="name") String name,
                                        @RequestParam(value="message") String message) throws InterruptedException, ExecutionException {
        Future<TextMessage[]> future = threadPool.submit(() -> backend.sendHTTP(name, message));
        return future.get();
    }

    @RequestMapping("/chatThreadPoolAsync")
    public AsyncResponseDTO chatThreadPoolAsync(@RequestParam(value="name") String name,
                                                @RequestParam(value="message") String message) throws InterruptedException, ExecutionException {
        threadPool.submit(() -> {
            try {
                Thread.sleep(1000);
                backend.sendHTTP(name, message);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        return new AsyncResponseDTO("HTTP call being executed asynchronously");
    }

    @RequestMapping("/chatCompletableFuture")
    public TextMessage[] chatCompletableFuture(@RequestParam(value="name") String name,
                                               @RequestParam(value="message") String message) throws InterruptedException, ExecutionException {
        // TODO add more actions one-by-one, check DT captures them all
        CompletableFuture<TextMessage[]> completableFuture = CompletableFuture.supplyAsync(() -> backend.sendHTTP(name, message));
        return completableFuture.get();
    }
}