package com.frontend;

import com.frontend.ws.MessageAddRequest;
import com.frontend.ws.MessageAddResponse;
import com.frontend.ws.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@RestController
@SpringBootApplication
@EnableJms
@Configuration
public class FrontendAppMain {

    public static final String ADD_MESSAGE_URL = "http://localhost:8988/addMessage";
    public static final String GET_MESSAGES_URL = "http://localhost:8988/getMessages";

    @Autowired
    private JmsTemplate jmsTemplate;

    public static void main(String[] args) {
		SpringApplication.run(FrontendAppMain.class, args);
	}

    @RequestMapping("/chatAsync")
    public void chatAsync(@RequestParam(value="name", defaultValue="noname") final String name,
                          @RequestParam(value="message", defaultValue="nomessage") final String message) {
        jmsTemplate.send(new ActiveMQQueue("chatQueue"), new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(name + "|" + message);
            }
        });
    }

	@RequestMapping("/chat")
	public TextMessage[] chat(@RequestParam(value="name", defaultValue="noname") String name,
                              @RequestParam(value="message", defaultValue="nomessage") String message) {
		RestTemplate template = new RestTemplate();

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

    @Bean
    public JmsTemplate getTemplate() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}