package com.frontend.socket;

import com.frontend.service.ChatBackendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class SocketService extends Thread {

    private final ServerSocket serverSocket;

    @Autowired
    private ChatBackendService chatService;

    public SocketService() throws IOException {
        this.serverSocket = new ServerSocket(8990);
        setName("newConnectionThread");
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new SocketThread(socket, chatService).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
