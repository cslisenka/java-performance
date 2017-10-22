package com.backend.api;

import com.backend.service.ChatDAO;
import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;

public class TCPSocketThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(TCPSocketService.class);

    private final Socket socket;
    private final ChatDAO dao;

    public TCPSocketThread(Socket socket, ChatDAO dao) throws IOException {
        this.socket = socket;
        this.dao = dao;
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

                dao.add(data[0]); // name and message
                send(socket, dao.getAll().toString());
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
}