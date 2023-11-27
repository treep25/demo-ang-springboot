package com.demo.backend.auth.conreoller;

import com.demo.backend.auth.AuthRequest;
import com.demo.backend.auth.RegRequest;
import com.demo.backend.user.mapper.UserMapper;
import com.demo.backend.user.model.User;
import com.demo.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(userService.login(authRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegRequest authRequest) {
        userService.save(authRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("me/info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal User user) {

        return ResponseEntity.ok(userMapper.convertToUserDto(user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(userService.refreshToken(refreshToken));
    }
}
