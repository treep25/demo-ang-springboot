package com.demo.backend.auth.oauth2.google;

import com.demo.backend.auth.oauth2.UrlDto;
import com.demo.backend.user.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wildfly.common.annotation.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class GoogleOAuth2Controller {

    @Value("${stripe.security.oauth2.resourceserver.google.opaque-token.client-id}")
    private String clientId;

    @Value("${stripe.security.oauth2.resourceserver.google.opaque-token.client-secret}")
    private String clientSecret;
    private final UserService userService;
    @Value("${stripe.security.oauth2.resourceserver.refirect-uri}")
    private String redirectUri;

    @GetMapping("/auth/login/oauth2")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal OAuth2IntrospectionAuthenticatedPrincipal user, @NotNull HttpServletRequest httpServletRequest) {
        if (user != null) {

            return ResponseEntity.ok(userService.loginViaGoogleOAuth2(user, httpServletRequest.getRemoteAddr()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated via OAuth2");
    }

    @GetMapping("/auth/url")
    public ResponseEntity<?> auth() throws MalformedURLException {
        String url = new GoogleAuthorizationCodeRequestUrl(
                clientId,
                redirectUri,
                List.of("email", "profile", "openid", "https://www.googleapis.com/auth/calendar.readonly"))
                .build();

        return ResponseEntity.ok(new UrlDto(url));
    }

    @GetMapping("/auth/callback")
    public ResponseEntity<?> authCallback(@RequestParam("code") String code) throws IOException {
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
}
