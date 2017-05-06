package com.frontend.to.backend.ribbon;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

public class RibbonConfiguration {

//    @Autowired
//    IClientConfig ribbonClientConfig;

//    @Bean
//    public IPing ribbonPing(IClientConfig config) {
//        return new PingUrl();
//    }
//
//    @Bean
//    public IRule ribbonRule() {
//        return new WeightedResponseTimeRule();
//    }

    @Bean
    public ILoadBalancer ribbonConfig() {
        List<Server> servers = Arrays.asList(
                new Server("localhost:8988"),
                new Server("localhost:8987"));

        return LoadBalancerBuilder.newBuilder()
                .withRule(new WeightedResponseTimeRule())
                .buildFixedServerListLoadBalancer(servers);
    }
}
