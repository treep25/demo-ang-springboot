package com.demo.backend.support.messages.service;

import com.demo.backend.support.messages.dto.MessageDto;
import com.demo.backend.support.messages.model.Message;
import com.demo.backend.support.messages.model.MessageStatus;
import com.demo.backend.support.messages.repository.MessageRepository;
import com.demo.backend.user.model.User;
import com.demo.backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private MessageRepository messageRepositoryMock;
    @InjectMocks
    private MessageService messageServiceMock;

    private User userSenderPlug = User
            .builder()
            .email("some_sender@gmail.com")
            .build();

    private User userRecipientPlug = User
            .builder()
            .email("some_recipient@gmail.com")
            .build();

    @Test
    void text() {
        UUID randomUIID = UUID.randomUUID();
        MessageDto messageDtoStub = MessageDto
                .builder()
                .content("Some content")
                .recipient("some_recipient@gmail.com")
                .build();

        Message message = Message
                .builder()
                .content(messageDtoStub.getContent())
                .dialogUUID(randomUIID)
                .messageStatus(MessageStatus.UNREAD)
                .recipient(userRecipientPlug)
                .sender(userSenderPlug)
                .build();

        when(userRepositoryMock.findByEmail(messageDtoStub.getRecipient()))
                .thenReturn(Optional.of(userRecipientPlug));
        when(messageRepositoryMock.findDialogUUIDBySenderAndRecipient(userSenderPlug, userRecipientPlug))
                .thenReturn(Optional.of(randomUIID));
        when(messageRepositoryMock.save(message))
                .thenReturn(message);

        messageServiceMock.text(messageDtoStub, userSenderPlug);
    }

    @Test
    void findConversation() {
        UUID randomUIID = UUID.randomUUID();
        String emailToPlug = "emialTo@Gmail.com";

        when(userRepositoryMock.findByEmail(emailToPlug))
                .thenReturn(Optional.of(userRecipientPlug));
        when(messageRepositoryMock.findDialogUUIDBySenderAndRecipient(userSenderPlug, userRecipientPlug))
                .thenReturn(Optional.of(randomUIID));
        List<Message> someContent = List.of(Message
                .builder()
                .content("some content")
                .dialogUUID(randomUIID)
                .messageStatus(MessageStatus.UNREAD)
                .recipient(userRecipientPlug)
                .sender(userSenderPlug)
                .build());

        when(messageRepositoryMock.findAllByDialogUUID(randomUIID))
                .thenReturn(someContent);
        when(messageRepositoryMock.saveAll(someContent))
                .thenReturn(someContent);

        List<Message> conversation = messageServiceMock.findConversation(userSenderPlug, emailToPlug);

        assertEquals(someContent, conversation);
    }

    @Test
    void getAllUnreadMessagesWith() {
        UUID randomUIID = UUID.randomUUID();
        List<Message> someContent = List.of(Message
                .builder()
                .content("some content")
                .dialogUUID(randomUIID)
                .messageStatus(MessageStatus.UNREAD)
                .recipient(userRecipientPlug)
                .sender(userSenderPlug)
                .build());

        when(userRepositoryMock.findByEmail(userRecipientPlug.getEmail()))
                .thenReturn(Optional.of(userRecipientPlug));
        when(messageRepositoryMock.findDialogUUIDBySenderAndRecipient(userSenderPlug, userRecipientPlug))
                .thenReturn(Optional.of(randomUIID));
        when(messageRepositoryMock.findAllByDialogUUID(randomUIID))
                .thenReturn(someContent);

        long allUnreadMessagesWith = messageServiceMock.getAllUnreadMessagesWith(userSenderPlug, userRecipientPlug.getEmail());

        assertEquals(allUnreadMessagesWith, 1);
    }
}