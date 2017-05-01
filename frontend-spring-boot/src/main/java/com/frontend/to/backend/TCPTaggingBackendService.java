package com.frontend.to.backend;

import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;

@Component
public class TCPTaggingBackendService {

    public String chatSync(final String name, final String message) throws IOException {
        try {
            DynaTraceADKFactory.initialize();
            Tagging tagging = DynaTraceADKFactory.createTagging();
            String requestTag = tagging.getTagAsString();
            tagging.linkClientPurePath(false, requestTag);

            try (Socket socket = new Socket("localhost", 8991)) {
                send(name + "|" + message + "|" + requestTag, socket);
                return receive(socket);
            }
        } finally {
            DynaTraceADKFactory.uninitialize();
        }
    }

    private void send(String message, Socket socket) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.println(message);
        writer.flush();
    }

    private String receive(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return reader.readLine();
    }
}
