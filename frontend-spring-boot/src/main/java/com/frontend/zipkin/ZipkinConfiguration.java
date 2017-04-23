package com.frontend.zipkin;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.github.kristofa.brave.spring.BraveClientHttpRequestInterceptor;
import com.github.kristofa.brave.spring.ServletHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Reporter;
import zipkin.reporter.Sender;
import zipkin.reporter.okhttp3.OkHttpSender;

import java.util.ArrayList;
import java.util.List;

@Configuration
// import as the interceptors are annotation with javax.inject and not automatically wired
@Import({BraveClientHttpRequestInterceptor.class, ServletHandlerInterceptor.class})
public class ZipkinConfiguration extends WebMvcConfigurerAdapter {

    @Bean Sender sender() {
        return OkHttpSender.create("http://localhost:9411/api/v1/spans");
    }

    /** Configuration for how to buffer spans into messages for Zipkin */
    @Bean Reporter<Span> reporter() {
//        return new LoggingReporter();
        // uncomment to actually send to zipkin
        return AsyncReporter.builder(sender()).build();
    }

    @Bean Brave brave() {
        return new Brave.Builder("fromtend-spring-boot").reporter(reporter()).build();
    }

    // decide how to name spans. By default they are named the same as the http method.
    @Bean SpanNameProvider spanNameProvider() {
        return new DefaultSpanNameProvider();
    }

    @Autowired
    private ServletHandlerInterceptor serverInterceptor;

    @Autowired
    private BraveClientHttpRequestInterceptor clientInterceptor;

    // All components must use modified RestTemplate
    @Bean RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
        interceptors.add(clientInterceptor);
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    // adds tracing to the application-defined web controllers
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serverInterceptor);
    }
}