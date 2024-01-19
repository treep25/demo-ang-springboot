package com.demo.backend.support.messages.service;

import com.demo.backend.support.messages.dto.MessageDto;
import com.demo.backend.support.messages.model.GroupMessage;
import com.demo.backend.support.messages.model.MessageStatus;
import com.demo.backend.support.messages.repository.GroupMessageRepository;
import com.demo.backend.user.model.User;
import com.demo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupMessageService {

    private final GroupMessageRepository messageRepository;
    private final UserRepository userRepository;

    public List<GroupMessage> getAllGroups(User user) {
        Set<GroupMessage> allBySender = new HashSet<>(messageRepository.findAllBySender(user));
        allBySender.addAll(new HashSet<>(messageRepository.findAllByRecipients(user)));
        return allBySender.stream()
                .collect(Collectors.toMap
                        (
                                GroupMessage::getDialogUUID,
                                Function.identity(),
                                (existing, replacement) -> existing)
                )
                .values().stream().sorted(Comparator.comparing(GroupMessage::getSentDateTime)).collect(Collectors.toList());
    }

    public List<GroupMessage> findConversation(User user, List<String> recipients) {
        List<User> allByEmail = userRepository.findAllByEmailIn(recipients);

        Set<GroupMessage> groupMessageSet = new HashSet<>();

        //todo bad practise
        allByEmail.forEach(
                recipient -> {
                    groupMessageSet.addAll(messageRepository.findGroupMessageBySenderAndRecipient(user, recipient));
                    groupMessageSet.addAll(messageRepository.findGroupMessageByRecipientAndSender(user, recipient));
                }
        );

        return groupMessageSet.stream().sorted(Comparator.comparing(GroupMessage::getSentDateTime)).toList();
    }

    public void textToGroup(MessageDto messageDto, User user) {

        List<User> allByEmailIn = userRepository.findAllByEmailIn(messageDto.getRecipients());
        Set<UUID> firstBySenderAndRecipients = new HashSet<>();
        allByEmailIn.forEach(
                user1 -> firstBySenderAndRecipients.add(messageRepository.findFirstBySenderAndRecipients(user, user1))
        );

        GroupMessage buildGroupMessage;

        buildGroupMessage = GroupMessage
                .builder()
                .messageStatus(MessageStatus.UNREAD)
                .recipients(allByEmailIn)
                .sender(user)
                .content(messageDto.getContent())
                .dialogUUID(generateOrSetUUIDDialog(firstBySenderAndRecipients))
                .build();

        messageRepository.save(buildGroupMessage);
    }

    private UUID generateOrSetUUIDDialog(Set<UUID> firstBySenderAndRecipients) {
        if (!firstBySenderAndRecipients.isEmpty()) {
            return firstBySenderAndRecipients.stream().toList().get(0);
        }
        return UUID.randomUUID();
    }
}
