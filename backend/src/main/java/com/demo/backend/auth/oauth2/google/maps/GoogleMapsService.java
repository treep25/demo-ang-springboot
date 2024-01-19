package com.demo.backend.auth.oauth2.google.maps;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GoogleMapsService {
    private final WebClient webClient;

    public GoogleMapsService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://maps.googleapis.com").build();
    }

    private String apiKey = "AIzaSyBE-7VS9GreaO8qPuLlgcznJ_jUmvBSVc4";

    public String getMapImageUrl(double latitude, double longitude) {
        String baseUrl = "https://maps.googleapis.com/maps/api/staticmap";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("center", latitude + "," + longitude)
                .queryParam("zoom", 14)
                .queryParam("size", "400x400")
                .queryParam("key", apiKey);

        return builder.toUriString();
    }

    public String getGeocodingData(String address) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/maps/api/geocode/json")
                        .queryParam("address", address)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


    public String getDirectionsData(double originLat, double originLng, double destinationLat, double destinationLng) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/maps/api/directions/json")
                        .queryParam("origin", String.format("%f,%f", originLat, originLng))
                        .queryParam("destination", String.format("%f,%f", destinationLat, destinationLng))
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
