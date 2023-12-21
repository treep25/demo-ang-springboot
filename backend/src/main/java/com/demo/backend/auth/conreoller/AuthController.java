package com.demo.backend.auth.conreoller;

import com.demo.backend.auth.AuthRequest;
import com.demo.backend.auth.RegRequest;
import com.demo.backend.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildfly.common.annotation.NotNull;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest, @NotNull HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(userService.login(authRequest, httpServletRequest.getRemoteAddr()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegRequest registerRequest) {
        userService.save(registerRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken, @NotNull HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(userService.refreshToken(refreshToken, httpServletRequest.getRemoteAddr()));
    }
}
