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
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class GmailService {

    private Gmail gmail;
    private static final String CURRENT_PRINCIPAL_ID = "me";
    private static final String LABEL_SENT = "SENT";
    private static final String MIME_MESSAGE_CONTENT_TYPE = "text/plain";

    private Map<Predicate<ByParamSearchingDto>, Function<ByParamSearchingDto, String>> predicateMap = Map.of(
            param -> isByParamExist(param.getBySender()), param -> "from:" + param.getBySender(),
            param -> isByParamExist(param.getBySubject()), param -> "subject:" + param.getBySubject(),
            param -> isByParamExist(param.getByContentSearchTerm()), ByParamSearchingDto::getByContentSearchTerm
    );

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

        ListMessagesResponse response = gmail
                .users()
                .messages()
                .list(CURRENT_PRINCIPAL_ID)
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

        return gmail
                .users()
                .messages()
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

    public void sendEmail(String to, String subject, String body, String token, MultipartFile attachment) throws IOException, MessagingException {

        gmail = setUpGmail(token);

        MimeMessage mimeMessage = createMimeMessage(to, subject, body, attachment);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        mimeMessage.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();

        Message message = new Message();
        message.setRaw(Base64.encodeBase64URLSafeString(bytes));

        gmail.users().messages().send(CURRENT_PRINCIPAL_ID, message).execute();
    }

    private MimeMessage createMimeMessage(String to, String subject, String body, MultipartFile attachment) throws MessagingException, IOException {
        Session session = Session.getDefaultInstance(new Properties(), null);

        MimeMessage email = new MimeMessage(session);
        email.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(body, MIME_MESSAGE_CONTENT_TYPE);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);


        if (attachment != null) {
            MimeBodyPart attachmentPart = new MimeBodyPart();

            attachmentPart.setContent(attachment.getBytes(), attachment.getContentType());
            attachmentPart.setFileName(attachment.getOriginalFilename());

            multipart.addBodyPart(attachmentPart);
        }

        email.setContent(multipart);

        return email;
    }


    private boolean isByParamExist(String byParam) {
        return byParam != null && !byParam.isBlank() && !byParam.isEmpty();
    }

    public List<Message> listMessagesByParam(ByParamSearchingDto byParamSearchingDto, String token) throws IOException {
        gmail = setUpGmail(token);

        AtomicReference<String> query = new AtomicReference<>("");

        predicateMap.forEach(
                (param, dto) -> {
                    if (param.test(byParamSearchingDto)) {
                        query.set(dto.apply(byParamSearchingDto));
                    }
                }
        );

        return gmail.users().messages()
                .list(CURRENT_PRINCIPAL_ID)
                .setQ(query.get())
                .setMaxResults(10L)
                .execute()
                .getMessages()
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

    public List<Message> getUnreadMessages(String token) throws IOException {
        gmail = setUpGmail(token);

        String query = "is:unread";
        return gmail
                .users()
                .messages()
                .list(CURRENT_PRINCIPAL_ID)
                .setQ(query)
                .execute()
                .getMessages()
                .stream()
                .map(message -> {
                    try {
                        return getMessage(token, message.getId());
                    } catch (IOException e) {
                        throw new RuntimeException();
                    }
                })
                .toList();
    }

    public List<Message> searchEmailsByDate(Date startDate, String token) throws IOException {
        gmail = setUpGmail(token);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String formattedStartDate = dateFormat.format(startDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date nextDay = calendar.getTime();
        String formattedEndDate = dateFormat.format(nextDay);

        String query = String.format("after:%s before:%s", formattedStartDate, formattedEndDate);

        return gmail
                .users()
                .messages()
                .list(CURRENT_PRINCIPAL_ID)
                .setQ(query)
                .execute()
                .getMessages()
                .stream()
                .map(message -> {
                    try {
                        return getMessage(token, message.getId());
                    } catch (IOException e) {
                        throw new RuntimeException();
                    }
                })
                .toList();
    }
}
