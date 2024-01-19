package com.demo.backend.support.messages.service;

import com.demo.backend.support.messages.dto.MessageDto;
import com.demo.backend.support.messages.model.Message;
import com.demo.backend.support.messages.model.MessageStatus;
import com.demo.backend.support.messages.repository.MessageRepository;
import com.demo.backend.user.model.User;
import com.demo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public void text(MessageDto messageDto, User user) {
        User recipient = userRepository.findByEmail(messageDto.getRecipient()).orElseThrow();

        UUID dialogUUID = messageRepository.findDialogUUIDBySenderAndRecipient(user, recipient).orElse(UUID.randomUUID());

        Message message = Message
                .builder()
                .content(messageDto.getContent())
                .dialogUUID(dialogUUID)
                .messageStatus(MessageStatus.UNREAD)
                .recipient(recipient)
                .sender(user)
                .build();

        messageRepository.save(message);
    }

    public List<Message> findConversation(User sender, String emailTo) {

        User recipient = userRepository.findByEmail(emailTo).orElseThrow();
        Optional<UUID> dialogUUIDBySenderAndRecipient = messageRepository.findDialogUUIDBySenderAndRecipient(sender, recipient);

        if (dialogUUIDBySenderAndRecipient.isEmpty()) {
            text(MessageDto.builder().content("").recipient(emailTo).build(), sender);
        }

        List<Message> allByDialogUUID = messageRepository.findAllByDialogUUID(messageRepository.findDialogUUIDBySenderAndRecipient(sender, recipient).orElseThrow());

        allByDialogUUID
                .stream()
                .filter(message -> message.getRecipient().getId() == sender.getId())
                .forEach(message -> message.setMessageStatus(MessageStatus.READ));

        messageRepository.saveAll(allByDialogUUID);
        return allByDialogUUID;
    }

    public long getAllUnreadMessagesWith(User currentUser, String email) {
        User with = userRepository.findByEmail(email).orElseThrow();
        UUID uuid = messageRepository.findDialogUUIDBySenderAndRecipient(currentUser, with).orElseThrow();

        long countAmountOfUnreadMessagesOfCurrentConversation = messageRepository
                .findAllByDialogUUID(uuid)
                .stream()
                .filter(message -> message.getRecipient().getId() == currentUser.getId())
                .filter(message -> message.getMessageStatus().equals(MessageStatus.UNREAD))
                .count();

        return countAmountOfUnreadMessagesOfCurrentConversation;
    }
}
