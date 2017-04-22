package com.frontend.tcp;

import com.frontend.to.backend.HttpBackendService;
import com.frontend.model.TextMessage;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Calendar;

public class SocketThread extends Thread {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final HttpBackendService chatService;

    public SocketThread(Socket socket, HttpBackendService chatService) throws IOException {
        this.socket = socket;
        this.chatService = chatService;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream());
        setName("socketThread_" + socket.getRemoteSocketAddress());
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected() && !socket.isClosed()) {
                String request = in.readLine();
                if ("exit".equals(request)) {
                    break;
                }

                callChatSync(request);
//                echo(request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void callChatSync(String request) throws IOException {
        String[] parts = request.split("\\|");
        if (parts.length == 2) {
            String name = parts[0];
            String message = parts[1];

            send("request name=" + name + ", message=" + message);

            TextMessage[] response = chatService.chatSync(name, message);
            send(Arrays.toString(response));

        } else {
            send("wrong request: " + request);
        }
    }

    private void echo(String request) throws IOException {
        send(request + ", server time=" + Calendar.getInstance().getTime());
    }

    private void send(String response) {
        out.println(response);
        out.flush();
    }
}