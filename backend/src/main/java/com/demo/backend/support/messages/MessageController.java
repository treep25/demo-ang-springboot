package com.demo.backend.support.messages;

import com.demo.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/texting")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping()
//    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> text(@AuthenticationPrincipal User user, @RequestBody MessageDto messageDto) {

        messageService.text(messageDto, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("find/conversation/{recipientEmail}")
//    @PreAuthorize("@permissionCheck.hasPermission(#user)")
    public ResponseEntity<?> findConversation(@AuthenticationPrincipal User user, @PathVariable String recipientEmail) {

        return ResponseEntity.ok(messageService.findConversation(user, recipientEmail));
    }
}
