package com.demo.backend.auth.imap;

import jakarta.mail.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class ImapMailService {

    private final JavaMailSender javaMailSender;

    public ImapMailService(@Qualifier("javaMailSenderImap") JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(String to, String subject, String body, String email) {

    }

    public List<Message> receiveUnreadMail() {
        List<Message> unreadMessages = new ArrayList<>();
        try {
            if (javaMailSender instanceof JavaMailSenderImpl mailSender) {
                Properties properties = mailSender.getJavaMailProperties();

                Session session = Session.getInstance(properties);
                Store store = session.getStore("imap");
                store.connect(mailSender.getHost(), mailSender.getPort(), mailSender.getUsername(), mailSender.getPassword());

                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_WRITE);


                Message[] messages = inbox.getMessages();


                for (Message message : messages) {
                    if (!message.isSet(Flags.Flag.SEEN)) {
                        unreadMessages.add(message);
                    }
                }

                inbox.close(false);
                store.close();
            }

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return unreadMessages;
    }
}