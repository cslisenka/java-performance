package com.app2;

import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPSocketThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(TCPSocketReceiver.class);

    private final Socket socket;
    private final JdbcTemplate jdbcTemplate;
    private final App2Controller controller;

    public TCPSocketThread(Socket socket, JdbcTemplate jdbcTemplate, App2Controller controller) {
        this.socket = socket;
        this.jdbcTemplate = jdbcTemplate;
        this.controller = controller;
    }

    public String[] receive(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String request = in.readLine();

        log.info("TCP RECEIVED {} [{}]", socket.getRemoteSocketAddress(), request);
        return request.split("\\|");
    }

    public void send(Socket socket, String response) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.println(response);
        out.flush();

        log.info("TCP SENT {} [{}]", socket.getRemoteSocketAddress(), response);
    }

    @Override
    public void run() {
        try {
            Tagging tagging = null;

            try {
                String[] data = receive(socket);

                if (data.length == 2) { // Mark Dynatrace pure path if we received tag
                    tagging = DynaTraceADKFactory.createTagging();
                    tagging.setTagFromString(data[1]); // Dynatrace tag
                    tagging.startServerPurePath();
                }

                controller.add(new MessageDTO(data[0]), null);
//                dao.add(data[0], data[1]); // name and message
                send(socket, "{ \"result\" : \"OK\" }");
            } finally {
                socket.close();

                if (tagging != null) {
                    tagging.endServerPurePath();
                }
            }
        } catch (IOException e) {
            log.error("Error processing TCP connection", e);
        }
    }

//    @Override
//    public void run() {
//        try {
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            PrintWriter out = new PrintWriter(socket.getOutputStream());
//
//            Tagging tagging = null;
//
//            try {
//                String request = in.readLine();
//                log.info("TCP RECEIVED {} [{}]", socket.getRemoteSocketAddress(), request);
//                String[] data = request.split("\\|");
//
//                if (data.length == 2) { // Mark Dynatrace pure path if we received tag
//                    tagging = DynaTraceADKFactory.createTagging();
//                    tagging.setTagFromString(data[1]); // Dynatrace tag
//                    tagging.startServerPurePath();
//                }
//
//                process(data[0]);
//                controller.add(new MessageDTO(data[0]), null);
//
//                String response = "{ \"result\" : \"OK\" }";
//                out.println(response);
//                out.flush();
//                log.info("TCP SENT {} [{}]", socket.getRemoteSocketAddress(), response);
//            } finally {
//                socket.close();
//                if (tagging != null) {
//                    tagging.endServerPurePath();
//                }
//            }
//        } catch (IOException e) {
//            log.error("Error processing TCP connection", e);
//        }
//    }

    public void process(String message) throws IOException {
        jdbcTemplate.update("INSERT INTO chat (message, timestamp) VALUES (?, NOW())", message);
    }
}