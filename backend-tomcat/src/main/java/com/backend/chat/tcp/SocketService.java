package com.backend.chat.tcp;

import com.backend.chat.dao.ChatDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class SocketService extends Thread {

    private final ServerSocket serverSocket;

    @Autowired
    private ChatDAO chatDAO;

    public SocketService() throws IOException {
        this.serverSocket = new ServerSocket(8991);
        setName("newConnectionThread");
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new SocketThread(socket, chatDAO).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
