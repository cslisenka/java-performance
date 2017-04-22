package com.frontend;

import com.frontend.to.backend.HttpBackendService;
import com.frontend.to.backend.TCPBackendService;
import com.frontend.model.TextMessage;
import com.frontend.to.backend.TCPTaggingBackendService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@SpringBootApplication
@EnableJms
@Configuration
public class FrontendAppMain {

    @Autowired
    private HttpBackendService httpService;

    @Autowired
    private TCPBackendService tcpService;

    @Autowired
    private TCPTaggingBackendService tcpTaggingService;

    public static void main(String[] args) {
		SpringApplication.run(FrontendAppMain.class, args);
    }

    @RequestMapping("/chatAsync")
    public void chatAsync(@RequestParam(value="name", defaultValue="noname") final String name,
                          @RequestParam(value="message", defaultValue="nomessage") final String message,
                          HttpServletRequest request) {
        httpService.chatAsync(name, message);
    }

	@RequestMapping("/chat")
	public TextMessage[] chat(@RequestParam(value="name", defaultValue="noname") String name,
                              @RequestParam(value="message", defaultValue="nomessage") String message,
                              HttpServletRequest request) {
        return httpService.chatSync(name, message);
	}

    @RequestMapping("/chatTcp")
    public String chatTcp(@RequestParam(value="name", defaultValue="noname") String name,
                          @RequestParam(value="message", defaultValue="nomessage") String message,
                          HttpServletRequest request) throws IOException {
        return tcpService.chatSync(name, message);
    }

    @RequestMapping("/chatTcpTagging")
    public String chatTcpTagging(@RequestParam(value="name", defaultValue="noname") String name,
                          @RequestParam(value="message", defaultValue="nomessage") String message,
                          HttpServletRequest request) throws IOException {
        return tcpTaggingService.chatSync(name, message);
    }

    @Bean
    public JmsTemplate getTemplate() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}