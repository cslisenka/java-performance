package com.frontend.service;

import com.backend.dto.AddMessageRequest;
import com.backend.dto.AddMessageResponse;
import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;
import com.frontend.FrontendMain;
import com.frontend.api.dto.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Queue;
import java.io.*;
import java.net.Socket;

@Component
//@RibbonClient(name = "ribbon-backend", configuration = FrontendMain.class)
public class BackendService {

    private static final Logger log = LoggerFactory.getLogger(BackendService.class);

//    public static final String ADD_MESSAGE_URL_RIBBON = "http://ribbon-backend/add";
//    public static final String GET_MESSAGES_URL_RIBBON = "http://ribbon-backend/getAll";

    public static final String ADD_MESSAGE_URL = "http://localhost:8988/add";
    public static final String GET_MESSAGES_URL = "http://localhost:8988/getAll";

    @Autowired
    private JmsTemplate jms;

    @Autowired
    private Queue chatQueue;

    @Autowired
    private RestTemplate http;

    public TextMessage[] callHTTP(final String name, final String message) {
        AddMessageResponse response = httpAddMessage(name, message);

        if (response.isSuccess()) {
            return httpGetMessages();
        } else {
            throw new RuntimeException("Backend error");
        }
    }

    public AddMessageResponse httpAddMessage(final String name, final String message) {
        log.info("HTTP POST {} [{}, {}]", ADD_MESSAGE_URL, name, message);
        return http.postForObject(ADD_MESSAGE_URL,
            new AddMessageRequest(name, message), AddMessageResponse.class);
    }

    public TextMessage[] httpGetMessages() {
        log.info("HTTP GET {}", GET_MESSAGES_URL);
        ResponseEntity<TextMessage[]> messages = http.getForEntity(GET_MESSAGES_URL, TextMessage[].class);
        return messages.getBody();
    }

    public void sendJMS(final String name, final String message) {
        final String messageText = name + "|" + message;
        jms.send(chatQueue, session -> session.createTextMessage(messageText));

        log.info("JMS SENT [{}] to {}", messageText, chatQueue);
    }

    public String sendTCP(final String name, final String message) throws IOException {
        try (Socket socket = connectTCP()) {
            sendTCP(name + "|" + message, socket);
            return receiveTCP(socket);
        }
    }

    public String sendTCPWithTagging(final String name, final String message) throws IOException {
        Tagging tagging = DynaTraceADKFactory.createTagging();
        String requestTag = tagging.getTagAsString();
        tagging.linkClientPurePath(false, requestTag);

        try (Socket socket = connectTCP()) {
            sendTCP(name + "|" + message + "|" + requestTag, socket);
            return receiveTCP(socket);
        }
    }

    private Socket connectTCP() throws IOException {
        return new Socket("localhost", 8985);
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