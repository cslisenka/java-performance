package com.frontend.to.backend;

import com.frontend.model.MessageAddRequest;
import com.frontend.model.MessageAddResponse;
import com.frontend.model.TextMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Component
public class HttpBackendService {

    public static final String ADD_MESSAGE_URL = "http://localhost:8988/addMessage";
    public static final String GET_MESSAGES_URL = "http://localhost:8988/getMessages";

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private RestTemplate template;

    public void chatAsync(final String name, final String message) {
        jmsTemplate.send(new ActiveMQQueue("chatQueue"), new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(name + "|" + message);
            }
        });
    }

    public TextMessage[] chatSync(final String name, final String message) {
        MessageAddResponse response = template.postForObject(ADD_MESSAGE_URL,
                new MessageAddRequest(name, message),
                MessageAddResponse.class);

        if (response.isSuccess()) {
            ResponseEntity<TextMessage[]> messages = template.getForEntity(GET_MESSAGES_URL, TextMessage[].class);
            return messages.getBody();
        } else {
            throw new RuntimeException("Backend error");
        }
    }
}