package com.demo.backend.auth.imap;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/tutorials")
@RequiredArgsConstructor
public class ImapMailController {

    private final ImapMailService imapMailService;


    @GetMapping("/receive")
    public ResponseEntity<?> receiveMail() {

        return ResponseEntity.ok(imapMailService.receiveUnreadMail());
    }
}
