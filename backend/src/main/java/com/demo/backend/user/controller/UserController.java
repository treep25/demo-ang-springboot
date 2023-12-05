package com.demo.backend.user.controller;

import com.demo.backend.order.OrderService;
import com.demo.backend.tutorial.model.Tutorial;
import com.demo.backend.user.mapper.UserMapper;
import com.demo.backend.user.model.User;
import com.demo.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final OrderService orderService;
    private final UserService userService;

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

    @GetMapping("/users")
    // todo  bad thing
//    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> getAllUsersExcludeCurrentPrinciple(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userMapper.convertUsersToUserDtos(userService.getAllUserExcludeCurrent(user.getId())));
    }

    @PostMapping("/{id}")
    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> getAllUsersExcludeCurrentPrinciple(
            @PathVariable("id") long id,
            @AuthenticationPrincipal User user) {

        userService.changeStatusOfUser(id);

        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/search")
    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> searchByFirstName(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName) {

        if (lastName == null && firstName == null) {
            throw new RuntimeException("Invalid Params");
        }
        List<User> users;
        if (firstName != null && !firstName.isBlank()) {
            users = userService.searchByFirstName(firstName);
        } else {
            users = userService.searchByLastName(lastName);
        }

        return ResponseEntity.ok(userMapper.convertUsersToUserDtos(users));
    }

    @GetMapping("/enabled")
    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> getAllEnabledUsers(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userMapper.convertUsersToUserDtos(userService.getAllEnabledUsers(user.getId())));
    }

    @DeleteMapping("/clear-orders")
    public ResponseEntity<?> clearOrders(@AuthenticationPrincipal User user) {
        userService.clearOrders(user);
        return ResponseEntity.noContent().build();
    }
}
