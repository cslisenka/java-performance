package com.backend.chat.tcp;

import com.backend.chat.dao.ChatDAO;
import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(SocketService.class);

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final ChatDAO chatDAO;

    private Tagging tagging;

    public SocketThread(Socket socket, ChatDAO chatDAO) throws IOException {
        this.socket = socket;
        this.chatDAO = chatDAO;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream());
        setName("socketThread_" + socket.getRemoteSocketAddress());
    }

    @Override
    public void run() {
        try {
            String request = in.readLine();

            log.info("TCP RECEIVED " + socket.getRemoteSocketAddress() + " [" + request + "]");

            String dtTag = parseDynatraceTag(request);
            if (dtTag != null) {
                DynaTraceADKFactory.initialize();
                tagging = DynaTraceADKFactory.createTagging();
                tagging.setTagFromString(dtTag);
                tagging.startServerPurePath();
            }

            handle(request);

            if (dtTag != null) {
                tagging.endServerPurePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            DynaTraceADKFactory.uninitialize();
        }
    }

    private void handle(String request) throws IOException {
        String[] parts = request.split("\\|");
        if (parts.length >= 2) {
            String name = parts[0];
            String message = parts[1];
            chatDAO.addNewMessage(name, message);
            send(chatDAO.getAllMessages().toString());
        } else {
            send("wrong request: " + request);
        }
    }

    private void send(String response) {
        out.println(response);
        out.flush();

        log.info("TCP SEND " + socket.getRemoteSocketAddress() + " [" + response + "]");
    }

    private String parseDynatraceTag(String request) {
        String[] parts = request.split("\\|");
        if (parts.length == 3) {
            return parts[2];
        }
        return null;
    }
}