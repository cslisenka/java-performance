package com.backend.chat.rest;

import com.backend.chat.dao.ChatDAO;
import com.backend.chat.dao.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RestController
public class ChatWebService {

    private static final Logger log = LoggerFactory.getLogger(ChatWebService.class);

    @Autowired
    private ChatDAO dao;

    @ResponseBody
    @RequestMapping(value = "/addMessage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public AddMessageResponse addMessage(@RequestBody AddMessageRequest newMessage, HttpServletRequest request) {
        logTracingInfo("/addMessage", request);

        boolean result = dao.addNewMessage(newMessage.getName(), newMessage.getMessage());
        return new AddMessageResponse(result);
    }

    @ResponseBody
    @RequestMapping(value = "/getMessages", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatMessage> getAllMessages(HttpServletRequest request) {
        logTracingInfo("/getAllMessages", request);

        return dao.getAllMessages();
    }

    private void logTracingInfo(String methodName, HttpServletRequest request) {
        log.info(methodName +
            " Dynatrace [" + getHeaders("x-dynatrace", request) + "]," +
            " Zipkin [" + getHeaders("x-b3", request) + " ]");
    }

    private String getHeaders(String prefix, HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
                .filter((headerName) -> headerName.startsWith(prefix))
                .map((headerName) -> headerName + "=" + request.getHeader(headerName))
                .reduce((header1, header2) -> header1 + ", " + header2).get();
    }
}