package com.demo.backend.auth.saml;

import com.demo.backend.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/saml")
@RequiredArgsConstructor
public class SamlController {

    private final SamlTokenService samlTokenService;

    @GetMapping("/token")
    public ResponseEntity<?> listMessages(@AuthenticationPrincipal User user) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        return new ResponseEntity<>(samlTokenService.createSamlResponse(user), headers, HttpStatusCode.valueOf(200));
    }

}
