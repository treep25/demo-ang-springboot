package com.demo.backend.support.messages;

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

        return messageRepository.findAllByDialogUUID(messageRepository.findDialogUUIDBySenderAndRecipient(sender, recipient).orElseThrow());
    }
}
