package com.app1;

import com.app1.zipkin.ZipkinConfiguration;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Import(ZipkinConfiguration.class)
@SpringBootApplication
@EnableJms
@Configuration
//@RibbonClient(name = "ribbon-app2", configuration = FrontendMain.class)
public class App1Main {

    public static void main(String[] args) {
		SpringApplication.run(App1Main.class, args);
    }

//    @Bean
//    ILoadBalancer ribbon() {
//        List<Server> servers = Arrays.asList(
//            new Server("localhost:8988") //,
////            new Server("localhost:8987")
//        );
//
//        return LoadBalancerBuilder.newBuilder()
//                .withRule(new WeightedResponseTimeRule())
//                .buildFixedServerListLoadBalancer(servers);
//    }

    @Bean
    JmsTemplate jmsTemplate() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    // Uncomment when Zipkin configuration is disabled
//    @LoadBalanced
//    @Bean
//    RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    @Bean("threadPool")
    ExecutorService threadPool() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    Queue chatQueue() {
        return new ActiveMQQueue("chatQueue");
    }
}