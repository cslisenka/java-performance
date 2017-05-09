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
        String[] data = parse(jmsMessage); // name and message

        if (data != null && data.length == 2) {
            String name = data[0];
            String message = data[1];
            dao.add(name, message);
        }
    }

    private String[] parse(Message jmsMessage) {
        if (jmsMessage instanceof TextMessage) {
            try {
                String text = ((TextMessage) jmsMessage).getText();
                log(jmsMessage, text);
                return text.split("\\|");
            } catch (JMSException e) {
                log.error("Error parsing JMS message", e);
            }
        }

        return null;
    }

    private void log(Message message, String text) throws JMSException {
        log.info("JMS RECEIVED [{}] from {} Dynatrace [{}]",
                text, message.getJMSDestination(), message.getStringProperty("dtdTraceTagInfo"));
    }
}