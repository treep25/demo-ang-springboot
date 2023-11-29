package com.demo.backend.user.controller;

import com.demo.backend.order.OrderService;
import com.demo.backend.tutorial.model.Tutorial;
import com.demo.backend.user.mapper.UserMapper;
import com.demo.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final OrderService orderService;

    @GetMapping("me/info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal User user) {

        return ResponseEntity.ok(userMapper.convertToUserDto(user));
    }

    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestBody Tutorial tutorial, @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(orderService.createOrder(tutorial, user));
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrderOfCurrentPrincipal(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user.getOrder());
    }
}
