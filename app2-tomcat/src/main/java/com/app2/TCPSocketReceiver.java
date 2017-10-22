package com.app2;

import com.dynatrace.adk.DynaTraceADKFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPSocketReceiver extends Thread {

    private static final Logger log = LoggerFactory.getLogger(TCPSocketReceiver.class);

    private final ServerSocket serverSocket;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public TCPSocketReceiver(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        start();
    }

    @Override
    public void run() {
        log.info("Listening TCP connections on port {}", serverSocket.getLocalPort());

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new TCPSocketThread(socket, jdbcTemplate).start();
            } catch (IOException e) {
                log.error("Error processing socket", e);
            }
        }
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