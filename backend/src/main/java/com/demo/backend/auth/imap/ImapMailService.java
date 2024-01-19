package com.demo.backend.auth.imap;

import jakarta.mail.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class ImapMailService {

    private final JavaMailSender javaMailSender;
    private static final String INBOX = "INBOX";
    private static final String PROTOCOL = "imap";

    public ImapMailService(@Qualifier("javaMailSenderImap") JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(String to, String subject, String body, String email) {
        //TODO perform this
    }

    public List<Message> receiveUnreadMail() {
        List<Message> unreadMessages = new ArrayList<>();
        try {
            if (javaMailSender
                    instanceof JavaMailSenderImpl mailSender) {

                Properties properties = mailSender.getJavaMailProperties();

                Session session = Session.getInstance(properties);
                Store store = session.getStore(PROTOCOL);
                store.connect(mailSender.getHost(), mailSender.getPort(), mailSender.getUsername(), mailSender.getPassword());

                Folder inbox = store.getFolder(INBOX);
                inbox.open(Folder.READ_WRITE);

                Message[] messages = inbox.getMessages();

                isSetOnSeen(messages, unreadMessages);

                inbox.close(false);
                store.close();
            }

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return unreadMessages;
    }

    private void isSetOnSeen(Message[] messages, List<Message> unreadMessages) throws MessagingException {
        Arrays
                .stream(messages)
                .forEach(message -> {
                    try {
                        if (message.isSet(Flags.Flag.SEEN)) {
                            unreadMessages.add(message);
                        }
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}