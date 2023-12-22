package com.demo.backend.config.google;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class WebClientConfig {

    @Value("${stripe.security.oauth2.resourceserver.opaque-token.introspection-uri}")
    private String introspectUri = "https://www.googleapis.com/";

    @Bean
    public WebClient userInfoClient() {
        return WebClient.builder().baseUrl(introspectUri).build();
    }
}
