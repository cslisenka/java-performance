package com.frontend.api;

import com.backend.dto.AddMessageRequest;
import com.backend.dto.AddMessageResponse;
import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;
import com.frontend.api.dto.AsyncResponse;
import com.frontend.api.dto.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
public class ProtocolDemo {

    private static final Logger log = LoggerFactory.getLogger(ProtocolDemo.class);

    public static final String ADD_MESSAGE_URL = "http://localhost:8988/add";
    public static final String GET_MESSAGES_URL = "http://localhost:8988/getAll";

    @Autowired
    private JmsTemplate jms;

    @Autowired
    private Queue chatQueue;

    @Autowired
    private RestTemplate http;

    @RequestMapping("/chat")
    public TextMessage[] chat(@RequestParam(value="name") String name,
                              @RequestParam(value="message") String message) {
        log.info("HTTP POST {} [{}, {}]", ADD_MESSAGE_URL, name, message);
        AddMessageResponse response =  http.postForObject(ADD_MESSAGE_URL,
                new AddMessageRequest(name, message), AddMessageResponse.class);

        if (response.isSuccess()) {
            log.info("HTTP GET {}", GET_MESSAGES_URL);
            return http.getForEntity(GET_MESSAGES_URL, TextMessage[].class).getBody();
        } else {
            throw new RuntimeException("Backend error");
        }
    }

    @RequestMapping("/chatAsync")
    public AsyncResponse chatAsync(@RequestParam(value="name") String name,
                                   @RequestParam(value="message") String message) {
        final String messageText = name + "|" + message;
        jms.send(chatQueue, session -> session.createTextMessage(messageText));

        log.info("JMS SENT [{}] to {}", messageText, chatQueue);
        return new AsyncResponse("JMS message has been sent to Active MQ");
    }

    @RequestMapping(value = "/chatTcp")
    public String chatTcp(@RequestParam(value="name") String name,
                          @RequestParam(value="message") String message) throws IOException {
        try (Socket socket = new Socket("localhost", 8985)) {
            sendTCP(name + "|" + message, socket);
            return receiveTCP(socket);
        }
    }

    @RequestMapping("/chatTcpTagging")
    public String chatTcpTagging(@RequestParam(value="name") String name,
                                 @RequestParam(value="message") String message) throws IOException {
        Tagging tagging = DynaTraceADKFactory.createTagging();
        String requestTag = tagging.getTagAsString();
        tagging.linkClientPurePath(false, requestTag);

        try (Socket socket = new Socket("localhost", 8985)) {
            sendTCP(name + "|" + message + "|" + requestTag, socket);
            return receiveTCP(socket);
        }
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
}