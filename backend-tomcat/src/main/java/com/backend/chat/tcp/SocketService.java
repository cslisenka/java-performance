package com.backend.chat.tcp;

import com.backend.chat.dao.ChatDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketService extends Thread {

    private final ServerSocket serverSocket;

    @Autowired
    private ChatDAO chatDAO;

    public SocketService(int port) throws IOException {
        System.out.println("Opening TCP server socket at port " + port);
        this.serverSocket = new ServerSocket(port);
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
