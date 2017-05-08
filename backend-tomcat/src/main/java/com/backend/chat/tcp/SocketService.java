package com.backend.chat.tcp;

import com.backend.chat.dao.ChatDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketService extends Thread {

    private static final Logger log = LoggerFactory.getLogger(SocketService.class);

    private final ServerSocket serverSocket;

    @Autowired
    private ChatDAO chatDAO;

    public SocketService(int port) throws IOException {
        log.info("Opening TCP server socket at port " + port);
        this.serverSocket = new ServerSocket(port);
        setName("socketAcceptor");
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
