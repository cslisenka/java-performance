package com.frontend.to.backend;

import com.dynatrace.adk.DynaTraceADKFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;

@Component
public class TCPBackendService {

    // TODO logger

    public String chatSync(final String name, final String message) throws IOException {
        try (Socket socket = new Socket("localhost", 8991)) {
            send(name + "|" + message, socket);
            return receive(socket);
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
