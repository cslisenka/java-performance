package com.backend.chat.jms;

import com.backend.chat.dao.ChatDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component("chatMessageListener")
public class JMSMessageReceiver implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(JMSMessageReceiver.class);

    @Autowired
    private ChatDAO dao;

    @Override
    public void onMessage(Message jmsMessage) {
        if (jmsMessage instanceof TextMessage) {
            try {
                logTracingInfo(jmsMessage);

                String text = ((TextMessage) jmsMessage).getText();
                String name = text.split("\\|")[0];
                String message = text.split("\\|")[1];
                dao.addNewMessage(name, message);
            } catch (JMSException e) {
                log.error("Error processing JMS message", e);
            }
        }
    }

    private void logTracingInfo(Message message) throws JMSException {
        log.info("Received JMS from " + message.getJMSDestination() + " Dynatrace [" +
                message.getStringProperty("dtdTraceTagInfo") + "]");
    }
}
