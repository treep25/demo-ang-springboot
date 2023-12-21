package com.demo.backend.config.csrf;

import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:8081", allowedHeaders = {"X-CSRF-TOKEN"})
@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
public class CsrfController {

    @GetMapping
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
