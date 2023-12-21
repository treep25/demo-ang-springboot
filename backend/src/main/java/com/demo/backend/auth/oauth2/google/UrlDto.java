package com.demo.backend.auth.oauth2.google;

import lombok.Data;

import java.net.URI;

@Data
public class UrlDto {

    private final String uri;

    public UrlDto(String url) {
        this.uri = URI.create(url).toString();
    }
}
