package com.demo.backend.auth.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class OAuth2Controller {

    @GetMapping("/")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal OAuth2User user) {
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated via OAuth2");
    }

    @GetMapping("/homeless")
    public ResponseEntity<?> getUserInfoBad(@AuthenticationPrincipal OAuth2User user) {
        return ResponseEntity.ok("Troubles boubles");
    }

}
