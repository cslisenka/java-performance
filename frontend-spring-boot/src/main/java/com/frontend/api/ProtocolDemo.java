package com.frontend.api;

import com.frontend.api.dto.AsyncResponse;
import com.frontend.api.dto.TextMessage;
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
        return backend.callHTTP(name, message);
    }

    @RequestMapping("/chatAsync")
    public AsyncResponse chatAsync(@RequestParam(value="name") String name,
                                   @RequestParam(value="message") String message) {
        backend.sendJMS(name, message);
        return new AsyncResponse("JMS message has been sent to Active MQ");
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