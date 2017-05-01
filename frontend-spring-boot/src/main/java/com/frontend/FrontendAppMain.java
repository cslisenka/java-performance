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
import javax.xml.soap.Text;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@SpringBootApplication
@EnableJms
@Configuration
public class FrontendAppMain {

    private ExecutorService executorService = Executors.newCachedThreadPool();

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

    @RequestMapping("/chatNewThread")
    public TextMessage[] chatNewThread(@RequestParam(value="name", defaultValue="noname") String name,
                              @RequestParam(value="message", defaultValue="nomessage") String message,
                              HttpServletRequest request) throws InterruptedException {
        AtomicReference<TextMessage[]> result = new AtomicReference<>();
        // Dynotrace does not associate new thread with current pure path
        Thread newThread = new Thread(() -> {
            result.set(httpService.chatSync(name, message));
        });

        newThread.start();
        newThread.join();
        return result.get();
    }

    @RequestMapping("/chatThreadPool")
    public TextMessage[] chatThreadPool(@RequestParam(value="name", defaultValue="noname") String name,
                                       @RequestParam(value="message", defaultValue="nomessage") String message,
                                       HttpServletRequest request) throws InterruptedException, ExecutionException {
        Future<TextMessage[]> future = executorService.submit(() -> httpService.chatSync(name, message));
        return future.get();
    }

    @RequestMapping("/chatThreadPoolAsync")
    public String chatThreadPoolAsync(@RequestParam(value="name", defaultValue="noname") String name,
                                        @RequestParam(value="message", defaultValue="nomessage") String message,
                                        HttpServletRequest request) throws InterruptedException, ExecutionException {
        executorService.submit(() -> {
            try {
                Thread.sleep(1000);
                executorService.submit(() -> httpService.chatSync(name, message));
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        return "request in processing";
    }

    @RequestMapping("/chatCompletableFuture")
    public TextMessage[] chatCompletableFuture(@RequestParam(value="name", defaultValue="noname") String name,
                                        @RequestParam(value="message", defaultValue="nomessage") String message,
                                        HttpServletRequest request) throws InterruptedException, ExecutionException {
        CompletableFuture<TextMessage[]> future = CompletableFuture.supplyAsync(() -> httpService.chatSync(name, message));
        return future.get();
    }

    @RequestMapping("/chatTcp")
    public String chatTcp(@RequestParam(value="name", defaultValue="noname") String name,
                          @RequestParam(value="message", defaultValue="nomessage") String message,
                          HttpServletRequest request) throws IOException {
        return tcpService.chatSync(name, message);
    }

    // Custom probe
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