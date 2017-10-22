package com.frontend.api;

import com.backend.dto.AddMessageResponse;
import com.backend.dto.MessageDTO;
import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Queue;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class HTTPController {

    private static final Logger log = LoggerFactory.getLogger(HTTPController.class);

    public static final String MESSAGE_URL = "http://localhost:8988/message";

    @Autowired
    private JmsTemplate jms;

    @Autowired
    private Queue chatQueue;

    @Autowired
    private RestTemplate http;

    @Autowired
    @Qualifier("threadPool")
    private ExecutorService threadPool;

    @RequestMapping("/send")
    public MessageDTO[] send(@RequestParam(value = "message") String message) {
        log.info("HTTP POST {} [{}]", MESSAGE_URL, message);
        AddMessageResponse response =  http.postForObject(MESSAGE_URL,
                new MessageDTO(message), AddMessageResponse.class);

        if (response.isSuccess()) {
            log.info("HTTP GET {}", MESSAGE_URL);
            return http.getForEntity(MESSAGE_URL, MessageDTO[].class).getBody();
        } else {
            throw new RuntimeException("Backend error");
        }
    }

    @RequestMapping("/sendJMS")
    public MessageDTO sendJMS(@RequestParam(value = "message") String message) {
        jms.send(chatQueue, session -> session.createTextMessage(message));
        log.info("JMS SENT [{}] to {}", message, chatQueue);
        return new MessageDTO("JMS message has been sent to Active MQ");
    }

    @RequestMapping(value = "/sendTcp")
    public String sendTcp(@RequestParam(value = "message") String message) throws IOException {
        try (Socket socket = new Socket("localhost", 8985)) {
            sendTCP(message, socket);
            return receiveTCP(socket);
        }
    }

    @RequestMapping("/sendTcpTagging")
    public String sendTcpTagging(@RequestParam(value = "message") String message) throws IOException {
        Tagging tagging = DynaTraceADKFactory.createTagging();
        String requestTag = tagging.getTagAsString();
        tagging.linkClientPurePath(false, requestTag);

        try (Socket socket = new Socket("localhost", 8985)) {
            sendTCP(message + "|" + requestTag, socket);
            return receiveTCP(socket);
        }
    }

    @RequestMapping("/sendAsNewThread")
    public MessageDTO[] sendAsNewThread(@RequestParam(value = "message") String message) throws InterruptedException {
        AtomicReference<MessageDTO[]> result = new AtomicReference<>();
        // Dynatrace does not associate new thread with current pure path
        Thread newThread = new Thread(() -> {
            log.info("in new thread");
            result.set(send(message));
        });

        newThread.start();
        newThread.join();
        return result.get();
    }

    @RequestMapping("/sendToThreadPool")
    public MessageDTO[] sendToThreadPool(@RequestParam(value = "message") String message) throws Exception {
        Future<MessageDTO[]> future = threadPool.submit(() -> {
            log.info("in thread pool");
            return send(message);
        });
        return future.get();
    }

    @RequestMapping("/sendToThreadPoolAsync")
    public MessageDTO sendToThreadPoolAsync(@RequestParam(value = "message") String message) throws Exception {
        threadPool.submit(() -> {
            log.info("in thread pool + async invocation");
            delay(1500);
            send(message);
        });

        return new MessageDTO("HTTP call being executed asynchronously");
    }

    @RequestMapping("/sendAsCompletableFuture")
    public MessageDTO[] sendAsCompletableFuture(@RequestParam(value = "message") String message) throws Exception {
        return CompletableFuture
            .supplyAsync(() -> {
                log.info("in completable future");
                log.info("HTTP POST {} [{}]", MESSAGE_URL, message);
                return http.postForObject(MESSAGE_URL,
                        new MessageDTO(message), AddMessageResponse.class);
            })
            .thenApply((response) -> {
                log.info("HTTP GET {}", MESSAGE_URL);
                if (response.isSuccess()) {
                    return http.getForEntity(MESSAGE_URL, MessageDTO[].class).getBody();
                } else {
                    throw new RuntimeException("Backend error in completable future");
                }
            }).get();
    }

    private void sendTCP(String message, Socket socket) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.println(message);
        writer.flush();
        log.info("TCP SEND " + socket.getRemoteSocketAddress() + " [" + message + "]");
    }

    private String receiveTCP(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String result = reader.readLine();
        log.info("TCP RECEIVED " + socket.getRemoteSocketAddress() + " [" + result + "]");
        return result;
    }

    @PostConstruct
    public void init() {
        DynaTraceADKFactory.initialize();
    }

    @PreDestroy
    public void preDestroy() {
        DynaTraceADKFactory.uninitialize();
    }

    private static void delay(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}