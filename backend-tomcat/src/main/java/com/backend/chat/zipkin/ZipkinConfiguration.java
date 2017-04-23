package com.backend.chat.zipkin;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.github.kristofa.brave.spring.BraveClientHttpRequestInterceptor;
import com.github.kristofa.brave.spring.ServletHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Reporter;
import zipkin.reporter.Sender;
import zipkin.reporter.okhttp3.OkHttpSender;

@Configuration
// import as the interceptors are annotation with javax.inject and not automatically wired
@Import({BraveClientHttpRequestInterceptor.class, ServletHandlerInterceptor.class})
public class ZipkinConfiguration {

    /** Configuration for how to send spans to Zipkin */
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
        return new Brave.Builder("backend-tomcat").reporter(reporter()).build();
    }

    // decide how to name spans. By default they are named the same as the http method.
    @Bean SpanNameProvider spanNameProvider() {
        return new DefaultSpanNameProvider();
    }

    @Bean ServletHandlerInterceptor instrumentingInterceptor() {
        return ServletHandlerInterceptor.create(brave());
    }
}