package com.frontend;

import com.frontend.service.ChatBackendService;
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

    @Autowired
    private ChatBackendService chatService;

    public static void main(String[] args) {
		SpringApplication.run(FrontendAppMain.class, args);
    }

    @RequestMapping("/chatAsync")
    public void chatAsync(@RequestParam(value="name", defaultValue="noname") final String name,
                          @RequestParam(value="message", defaultValue="nomessage") final String message) {
        chatService.chatAsync(name, message);
    }

	@RequestMapping("/chat")
	public TextMessage[] chat(@RequestParam(value="name", defaultValue="noname") String name,
                              @RequestParam(value="message", defaultValue="nomessage") String message) {
        return chatService.chatSync(name, message);
	}

    @Bean
    public JmsTemplate getTemplate() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}