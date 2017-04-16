package com.backend.chat.jms;

import com.backend.chat.dao.ChatDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component("chatMessageListener")
public class JMSMessageReceiver implements MessageListener {

    @Autowired
    private ChatDAO dao;

    @Override
    public void onMessage(Message jmsMessage) {
        if (jmsMessage instanceof TextMessage) {
            try {
                String text = ((TextMessage) jmsMessage).getText();
                String name = text.split("\\|")[0];
                String message = text.split("\\|")[1];
                dao.addNewMessage(name, message);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
