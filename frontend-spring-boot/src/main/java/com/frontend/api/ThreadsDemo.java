package com.frontend.api;

import com.frontend.api.dto.AsyncResponse;
import com.frontend.api.dto.TextMessage;
import com.frontend.service.BackendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class ThreadsDemo {

    private static final Logger log = LoggerFactory.getLogger(ThreadsDemo.class);

    @Autowired
    private BackendService backendService;

    @Autowired
    @Qualifier("threadPool")
    private ExecutorService threadPool;

    @RequestMapping("/chatNewThread")
    public TextMessage[] chatNewThread(@RequestParam(value="name") String name,
                                       @RequestParam(value="message") String message) throws InterruptedException {

        // SHOW THREAD LOCALS
        AtomicReference<TextMessage[]> result = new AtomicReference<>();
        // Dynatrace does not associate new thread with current pure path
        Thread newThread = new Thread(() -> {
            log.info("in new thread");
            result.set(backendService.callHTTP(name, message));
        });

        newThread.start();
        newThread.join();
        return result.get();
    }

    @RequestMapping("/chatThreadPool")
    public TextMessage[] chatThreadPool(@RequestParam(value="name") String name,
                                        @RequestParam(value="message") String message) throws Exception {

        Future<TextMessage[]> future = threadPool.submit(() -> {
            log.info("in thread pool");
            return backendService.callHTTP(name, message);
        });
        return future.get();
    }

    @RequestMapping("/chatThreadPoolAsync")
    public AsyncResponse chatThreadPoolAsync(@RequestParam(value="name") String name,
                                             @RequestParam(value="message") String message) throws Exception {
        threadPool.submit(() -> {
            log.info("in thread pool + async invocation");
            delay(1500);
            backendService.callHTTP(name, message);
        });

        return new AsyncResponse("HTTP call being executed asynchronously");
    }

    @RequestMapping("/chatCompletableFuture")
    public TextMessage[] chatCompletableFuture(@RequestParam(value="name") String name,
                                               @RequestParam(value="message") String message) throws Exception {
        return CompletableFuture
            .supplyAsync(() -> {
                log.info("in completable future");
                return backendService.httpAddMessage(name, message);
            })
            .thenApply((response) -> {
                if (response.isSuccess()) {
                    return backendService.httpGetMessages();
                }

                throw new RuntimeException("Backend error in completable future");
            }).get();
    }

    public static void delay(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}