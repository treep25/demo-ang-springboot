package com.demo.backend.user.controller;

import com.demo.backend.order.service.OrderService;
import com.demo.backend.tutorial.model.Tutorial;
import com.demo.backend.user.SearchingRequest;
import com.demo.backend.user.mapper.UserMapper;
import com.demo.backend.user.model.User;
import com.demo.backend.user.service.UserService;
import com.itextpdf.text.DocumentException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping("me/info/report")
    public ResponseEntity<?> getUserInfoPdfReport(@AuthenticationPrincipal User user) throws DocumentException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "user_report.pdf");

        return new ResponseEntity<>(userService.generatePdfReport(user), headers, HttpStatus.OK);
    }

    @PostMapping("me/info/report/gmail")
    public ResponseEntity<?> sendUserInfoPdfReport(@AuthenticationPrincipal User user) throws DocumentException, MessagingException {
        userService.sendReportViaGmail(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestBody Tutorial tutorial, @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(orderService.createOrder(tutorial, user));
    }

    @PostMapping("/order/pay")
    public ResponseEntity<?> payOrder(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.payOrder(user));
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrderOfCurrentPrincipal(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user.getOrder());
    }

    @GetMapping("/users")
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

        if (firstName == null && lastName == null) {
            throw new IllegalArgumentException();
        }

        return ResponseEntity.ok(
                userMapper.convertUsersToUserDtos(
                        userService.searchByFirstNameOrLastName(
                                SearchingRequest
                                        .builder()
                                        .firstName(firstName)
                                        .lastName(lastName)
                                        .build())
                )
        );
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

    @GetMapping("/message/unread/{email}")
    public ResponseEntity<?> getAllUnreadMessagesOfCurrentDialod(@AuthenticationPrincipal User user, @PathVariable String email) {
        return ResponseEntity.ok(userService.getAllUnreadMessagesWith(user, email));
    }

    @GetMapping("/message/unread")
    public ResponseEntity<?> getAllUnreadMessages(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getAllUnreadMessagesOf(user));
    }

    @PostMapping("/pay/orders")
    public ResponseEntity<?> payOrderSuccessfully(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.payOrder(user));
    }

    @DeleteMapping("/delete/orders")
    public ResponseEntity<?> cancelOrder(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.cancelOrder(user));
    }
}
