package com.demo.backend.user.controller;

import com.demo.backend.user.mapper.UserMapper;
import com.demo.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    @GetMapping("me/info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal User user) {

        return ResponseEntity.ok(userMapper.convertToUserDto(user));
    }
}
