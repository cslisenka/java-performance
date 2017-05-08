package com.frontend.service;

import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;
import com.frontend.FrontendAppMain;
import com.frontend.dto.MessageAddRequest;
import com.frontend.dto.MessageAddResponse;
import com.frontend.dto.TextMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.jms.Queue;
import java.io.*;
import java.net.Socket;

@Component
@RibbonClient(name = "ribbon-backend", configuration = FrontendAppMain.class)
public class Backend {

    private static final Logger log = LoggerFactory.getLogger(Backend.class);

    public static final String ADD_MESSAGE_URL = "http://ribbon-backend/addMessage";
    public static final String GET_MESSAGES_URL = "http://ribbon-backend/getMessages";

    @Autowired
    private JmsTemplate jms;

    @Autowired
    private Queue chatQueue;

    @Autowired
    private RestTemplate http;

    public void sendJMS(final String name, final String message) {
        log.info("Sending JMS message to " + chatQueue);

        jms.send(chatQueue, session -> {
            return session.createTextMessage(name + "|" + message);
        });
    }

    public TextMessage[] sendHTTP(final String name, final String message) {
        log.info("HTTP POST " + ADD_MESSAGE_URL);
        MessageAddResponse response = http.postForObject(ADD_MESSAGE_URL,
                new MessageAddRequest(name, message), MessageAddResponse.class);

        if (response.isSuccess()) {
            log.info("HTTP GET " + GET_MESSAGES_URL);
            ResponseEntity<TextMessage[]> messages = http.getForEntity(GET_MESSAGES_URL, TextMessage[].class);
            return messages.getBody();
        } else {
            throw new RuntimeException("Backend error");
        }
    }

    public String sendTCP(final String name, final String message) throws IOException {
        try (Socket socket = connectTCP()) {
            sendTCP(name + "|" + message, socket);
            return receiveTCP(socket);
        }
    }

    public String sendTCPWithTagging(final String name, final String message) throws IOException {
        try {
            DynaTraceADKFactory.initialize();
            Tagging tagging = DynaTraceADKFactory.createTagging();
            String requestTag = tagging.getTagAsString();
            tagging.linkClientPurePath(false, requestTag);

            try (Socket socket = connectTCP()) {
                sendTCP(name + "|" + message + "|" + requestTag, socket);
                return receiveTCP(socket);
            }
        } finally {
            DynaTraceADKFactory.uninitialize();
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
}