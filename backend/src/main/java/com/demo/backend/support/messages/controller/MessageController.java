package com.demo.backend.support.messages.controller;

import com.demo.backend.support.messages.dto.MessageDto;
import com.demo.backend.support.messages.service.GroupMessageService;
import com.demo.backend.support.messages.service.MessageService;
import com.demo.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/texting")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final GroupMessageService groupMessageService;

    @PostMapping()
    public ResponseEntity<?> text(@AuthenticationPrincipal User user, @RequestBody MessageDto messageDto) {

        messageService.text(messageDto, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/find/conversation/{recipientEmail}")
    public ResponseEntity<?> findConversation(@AuthenticationPrincipal User user, @PathVariable String recipientEmail) {

        return ResponseEntity.ok(messageService.findConversation(user, recipientEmail));
    }

    @GetMapping("/group/messages")
    public ResponseEntity<?> getAllGroups(@AuthenticationPrincipal User user) {

        return ResponseEntity.ok(groupMessageService.getAllGroups(user));
    }

    @PostMapping("/find/conversation/groups")
    public ResponseEntity<?> findConversationOfGroups(@AuthenticationPrincipal User user, @RequestBody List<String> recipients) {
        return ResponseEntity.ok(groupMessageService.findConversation(user, recipients));
    }

    @PostMapping("/group/message")
    public ResponseEntity<?> textGroup(@AuthenticationPrincipal User user, @RequestBody MessageDto messageDto) {
        groupMessageService.textToGroup(messageDto, user);

        return ResponseEntity.ok().build();
    }

}
