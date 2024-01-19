package com.demo.backend.auth.oauth2.google.maps;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/google/map")
@RequiredArgsConstructor
public class GoogleMapsController {


    private final GoogleMapsService googleMapsService;

    @GetMapping()
    public ResponseEntity<?> getMap() {
        return ResponseEntity.ok(googleMapsService.getMapImageUrl(37.7749, -122.4194));
    }

    @GetMapping("/1")
    public ResponseEntity<?> getGeocodingData() {
        return ResponseEntity.ok(googleMapsService.getGeocodingData("1600 Amphitheatre Parkway, Mountain View, CA"));
    }

    @GetMapping("/2")
    public ResponseEntity<?> getDirectionsData() {
        return ResponseEntity.ok(googleMapsService.getDirectionsData(37.7749, -122.4194, 37.7749, -122.4194));
    }
}
