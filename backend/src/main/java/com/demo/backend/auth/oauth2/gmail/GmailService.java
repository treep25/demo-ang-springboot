package com.demo.backend.auth.oauth2.gmail;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class GmailService {

    private Gmail gmail;
    private static final String CURRENT_PRINCIPAL_ID = "me";
    private static final String LABEL_SENT = "SENT";

    private Gmail setUpGmail(String jwtToken) {
        GoogleCredential credential = new GoogleCredential().setAccessToken(jwtToken);

        return new Gmail.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Spring boot Angular OAuth2")
                .build();
    }

    public List<Message> listMessagesGlobalInfo(String token) throws IOException {
        return listMessagesMetaInfo(token)
                .stream()
                .map(message -> {
                    try {
                        return getMessage(token, message.getId());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }


    public List<Message> listMessagesMetaInfo(String token) throws IOException {
        gmail = setUpGmail(token);

        ListMessagesResponse response = gmail.users().messages().list(CURRENT_PRINCIPAL_ID)
                .setMaxResults(15L)
                .execute();

        return response.getMessages();
    }

    public Message getMessage(String token, String messageId) throws IOException {
        gmail = setUpGmail(token);

        return gmail.users().messages().get(CURRENT_PRINCIPAL_ID, messageId).execute();
    }

    public List<Message> listOutgoingMessages(String token) throws IOException {
        gmail = setUpGmail(token);

        return gmail.users().messages()
                .list(CURRENT_PRINCIPAL_ID)
                .setLabelIds(Collections.singletonList(LABEL_SENT))
                .execute().getMessages()
                .stream()
                .map(message -> {
                    try {
                        return getMessage(token, message.getId());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    public void sendEmail(String to, String subject, String body, String token) throws IOException, MessagingException {

        gmail = setUpGmail(token);

        MimeMessage mimeMessage = createMimeMessage(to, subject, body);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        mimeMessage.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();

        Message message = new Message();
        message.setRaw(Base64.encodeBase64URLSafeString(bytes));

        gmail.users().messages().send(CURRENT_PRINCIPAL_ID, message).execute();
    }

    private MimeMessage createMimeMessage(String to, String subject, String body) throws MessagingException {
        Session session = Session.getDefaultInstance(new Properties(), null);

        MimeMessage email = new MimeMessage(session);
        email.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(body, "text/plain");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        email.setContent(multipart);
        return email;
    }

}
