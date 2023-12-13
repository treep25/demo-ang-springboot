package com.demo.backend.support.messages;

import com.demo.backend.user.model.User;
import com.demo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupMessageService {

    private final GroupMessageRepository messageRepository;
    private final UserRepository userRepository;

    List<GroupMessage> getAllGroups(User user) {
        List<GroupMessage> allBySender = messageRepository.findAllBySender(user);
        allBySender.addAll(messageRepository.findAllByRecipients(user));
        return allBySender;
    }

    public List<GroupMessage> findConversation(User user, List<String> recipients) {
        List<User> allByEmail = userRepository.findAllByEmailIn(recipients);

        Set<GroupMessage> groupMessageSet = new HashSet<>();

        //todo bad practise
        allByEmail.forEach(
                recipient -> groupMessageSet.addAll(messageRepository.findGroupMessageBySenderAndRecipient(user, recipient))
        );

        groupMessageSet.stream().map(GroupMessage::getSender).map(User::getFirstName).forEach(System.err::println);

        return groupMessageSet.stream().toList();
    }

    public void textToGroup(MessageDto messageDto, User user) {

        List<User> allByEmailIn = userRepository.findAllByEmailIn(messageDto.getRecipients());

        GroupMessage build = GroupMessage
                .builder()
                .messageStatus(MessageStatus.UNREAD)
                .recipients(allByEmailIn)
                .sender(user)
                .content(messageDto.getContent())
                .dialogUUID(UUID.fromString("0xEFBBBF00000000000000000000000000"))
                .build();

        messageRepository.save(build);
    }
}
