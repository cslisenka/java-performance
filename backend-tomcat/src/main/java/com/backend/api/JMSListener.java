package com.backend.api;

import com.backend.service.ChatDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component("jmsListener")
public class JMSListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(JMSListener.class);

    @Autowired
    private ChatDAO dao;

    @Override
    public void onMessage(Message jmsMessage) {
        if (jmsMessage instanceof TextMessage) {
            try {
                String text = ((TextMessage) jmsMessage).getText();
                log.info("JMS RECEIVED [{}] from {} Dynatrace [{}]", text,
                        jmsMessage.getJMSDestination(), jmsMessage.getStringProperty("dtdTraceTagInfo"));

                dao.add(text);
            } catch (Exception e) {
                log.error("Error parsing JMS message", e);
            }
        }
    }
}