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
    private String googleIntrospectUri;
    @Value("${stripe.security.oauth2.resourceserver.facebook.opaque-token.introspection-uri}")
    private String facebookIntrospectUri;

    @Bean
    public WebClient googleWebClient() {
        return WebClient.builder().baseUrl(googleIntrospectUri).build();
    }

    @Bean
    public WebClient facebookWebClient() {
        return WebClient.builder().baseUrl(facebookIntrospectUri).build();
    }
}
