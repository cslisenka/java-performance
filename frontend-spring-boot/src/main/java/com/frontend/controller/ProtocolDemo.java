package com.frontend.controller;

import com.frontend.dto.AsyncResponseDTO;
import com.frontend.dto.TextMessage;
import com.frontend.service.Backend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ProtocolDemo {

    @Autowired
    private Backend backend;

    @RequestMapping("/chat")
    public TextMessage[] chat(@RequestParam(value="name") String name,
                              @RequestParam(value="message") String message) {
        return backend.sendHTTP(name, message);
    }

    @RequestMapping("/chatAsync")
    public AsyncResponseDTO chatAsync(@RequestParam(value="name") String name,
                                      @RequestParam(value="message") String message) {
        backend.sendJMS(name, message);
        return new AsyncResponseDTO("JMS message has been sent to Active MQ");
    }

    @RequestMapping(value = "/chatTcp")
    public String chatTcp(@RequestParam(value="name") String name,
                          @RequestParam(value="message") String message) throws IOException {
        return backend.sendTCP(name, message);
    }

    @RequestMapping("/chatTcpTagging")
    public String chatTcpTagging(@RequestParam(value="name") String name,
                                 @RequestParam(value="message") String message) throws IOException {
        return backend.sendTCPWithTagging(name, message);
    }
}