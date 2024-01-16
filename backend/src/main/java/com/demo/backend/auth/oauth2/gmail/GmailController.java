package com.demo.backend.auth.oauth2.gmail;

import com.demo.backend.auth.oauth2.UrlDto;
import com.demo.backend.user.model.User;
import com.demo.backend.user.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/gmail")
@RequiredArgsConstructor
public class GmailController {

    @Value("${stripe.security.oauth2.resourceserver.google.opaque-token.client-id}")
    private String clientId;
    @Value("${stripe.security.oauth2.resourceserver.google.opaque-token.client-secret}")
    private String clientSecret;
    private final GmailService gmailService;
    private String redirectUri = "http://localhost:8081/google/gmail";
    private final UserService userService;
    private static final String READONLY_SCOPE = "https://www.googleapis.com/auth/gmail.readonly";
    private static final String MODIFY_SCOPE = "https://www.googleapis.com/auth/gmail.modify";
    private static final String SEND_SCOPE = "https://www.googleapis.com/auth/gmail.compose";
    private static final String INSERT_SCOPE = "https://www.googleapis.com/auth/gmail.insert";
    private static final String AUTH_DIFF_USERS_SCOPE1 = "https://www.googleapis.com/auth/gmail.settings.basic";
    private static final String AUTH_DIFF_USERS_SCOPE2 = "https://www.googleapis.com/auth/gmail.settings.sharing";

    @GetMapping("/messages")
    public ResponseEntity<?> listMessages(@AuthenticationPrincipal User user) throws IOException {
        List<Message> messages = gmailService.listMessagesGlobalInfo(user.getGoogleAccessTokenNotRequered());
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/message")
    public ResponseEntity<?> getMessage(@AuthenticationPrincipal User user, @RequestParam String messageId) throws IOException {
        Message message = gmailService.getMessage(user.getGoogleAccessTokenNotRequered(), messageId);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmail(
            @AuthenticationPrincipal User user,
            @RequestBody SendEmailDto sendEmailDto) throws IOException, MessagingException {

        gmailService.sendEmail(sendEmailDto.getTo(), sendEmailDto.getSubject(), sendEmailDto.getBody(), user.getGoogleAccessTokenNotRequered());
        return ResponseEntity.ok("Email sent successfully");
    }

    @GetMapping("/messages/outgoing")
    public ResponseEntity<?> getOutgoingMessages(
            @AuthenticationPrincipal User user) throws IOException {

        return ResponseEntity.ok(gmailService.listOutgoingMessages(user.getGoogleAccessTokenNotRequered()));
    }

    @GetMapping("/auth/url")
    public ResponseEntity<?> auth() throws MalformedURLException {
        String url = new GoogleAuthorizationCodeRequestUrl(
                clientId,
                redirectUri,
                List.of(READONLY_SCOPE, MODIFY_SCOPE, SEND_SCOPE, INSERT_SCOPE, AUTH_DIFF_USERS_SCOPE1, AUTH_DIFF_USERS_SCOPE2))
                .build();

        return ResponseEntity.ok(new UrlDto(url));
    }

    @GetMapping("/auth/callback")
    public ResponseEntity<?> authCallback
            (
                    @RequestParam("code") String code) throws IOException {

        GoogleTokenResponse execute = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                new GsonFactory(),
                clientId,
                clientSecret,
                code,
                redirectUri

        ).execute();
        String accessToken = execute.getAccessToken();

        return ResponseEntity.ok(accessToken);
    }

    @GetMapping("/save/access-token")
    public ResponseEntity<?> setTokenPrincipal
            (
                    @RequestParam("token") String token,
                    @AuthenticationPrincipal User user) {

        user.setGoogleAccessTokenNotRequered(token);

        userService.updateGoogleAccessToken(user);
        return ResponseEntity.ok().build();
    }


}
