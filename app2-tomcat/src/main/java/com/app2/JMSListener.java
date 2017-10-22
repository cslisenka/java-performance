package com.app2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component("jmsListener")
public class JMSListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(JMSListener.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void onMessage(Message msg) {
        if (msg instanceof TextMessage) {
            try {
                String text = ((TextMessage) msg).getText();
                log.info("JMS RECEIVED [{}] from {} Dynatrace [{}]", text,
                        msg.getJMSDestination(), msg.getStringProperty("dtdTraceTagInfo"));
                jdbcTemplate.update("INSERT INTO chat (message, timestamp) VALUES (?, NOW())", text);
            } catch (Exception e) {
                log.error("Error parsing JMS message", e);
            }
        }
    }
}