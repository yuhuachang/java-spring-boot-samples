package com.example.restlogging.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestLoggingFilterConfig {

    @Bean
    public HttpRequestResponseLoggingFilter filter() {
        return new HttpRequestResponseLoggingFilter();
    }
}
