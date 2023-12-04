package com.demo.backend.request;

import com.demo.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/request")
@RequiredArgsConstructor
public class RequestProcessingController {

    private final RequestProcessingService requestService;

    @PostMapping
    public ResponseEntity<?> createRequestUnBlocking(@RequestBody RequestDto requestDto) {
        requestService.createRequest(requestDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> getAllRequestWithUsers(@AuthenticationPrincipal User user) {

        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @PostMapping("close/{id}")
    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> closeRequest(@AuthenticationPrincipal User user, @PathVariable long id) {
        requestService.closeRequest(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("cancele/{id}")
    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> canceleRequest(@AuthenticationPrincipal User user, @PathVariable long id) {
        requestService.canceleRequest(id);

        return ResponseEntity.ok().build();
    }
}
