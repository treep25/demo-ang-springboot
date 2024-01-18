package com.demo.backend.config.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class ImapMailClient {

    private String mailHost = "imap.gmail.com";
    private int port = 993;
    @Value("${mail.username}")
    private String mailUsername;
    @Value("${mail.password}")
    private String mailPassword;

    @Bean
    public JavaMailSender javaMailSenderImap() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(port);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.imap.auth", true);
        props.put("mail.imap.starttls.enable", true);
        props.put("mail.imap.host", mailHost);
        props.put("mail.imap.port", port);
        props.put("mail.imap.user", mailUsername);
        props.put("mail.imap.password", mailPassword);
        return mailSender;
    }
}
