package com.backend.chat.rest;

import com.backend.chat.dao.ChatDAO;
import com.backend.chat.dao.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class ChatWebService {

    @Autowired
    private ChatDAO dao;

    @ResponseBody
    @RequestMapping(value = "/addMessage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public AddMessageResponse addMessage(@RequestBody AddMessageRequest newMessage, HttpServletRequest request) {
        System.out.println("addMessage"); // TODO configure logging
        boolean result = dao.addNewMessage(newMessage.getName(), newMessage.getMessage());
        return new AddMessageResponse(result);
    }

    @ResponseBody
    @RequestMapping(value = "/getMessages", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatMessage> getAllMessages(HttpServletRequest request) {
        System.out.println("getAllMessages"); // TODO configure logging
        return dao.getAllMessages();
    }
}