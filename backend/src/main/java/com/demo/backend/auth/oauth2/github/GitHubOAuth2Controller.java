package com.demo.backend.auth.oauth2.github;

import com.demo.backend.auth.oauth2.UrlDto;
import com.demo.backend.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.wildfly.common.annotation.NotNull;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class GitHubOAuth2Controller {

    @Value("${stripe.security.oauth2.resourceserver.git-hub.opaque-token.client-id}")
    private String clientId;

    @Value("${stripe.security.oauth2.resourceserver.git-hub.opaque-token.client-secret}")
    private String clientSecret;
    private final UserService userService;
    private String redirectUri = "http://localhost:8081/login";
    private String githubAccessTokenUrl = "https://github.com/login/oauth/access_token";

    @GetMapping("/auth/login/github/oauth2")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal OAuth2IntrospectionAuthenticatedPrincipal user, @NotNull HttpServletRequest httpServletRequest) {
        if (user != null) {

            return ResponseEntity.ok(userService.loginViaGithubOAuth2(user, httpServletRequest.getLocalAddr()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated via OAuth2");
    }

    @GetMapping("/auth/github/url")
    public ResponseEntity<?> auth() throws MalformedURLException {
        String url = "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=user,read:user,user:email";

        return ResponseEntity.ok(new UrlDto(url));
    }

    @GetMapping("/auth/github/callback")
    public String authCallback(@RequestParam("code") String code) {
        WebClient webClient = WebClient.create();
        String responseBody = webClient.post()
                .uri(githubAccessTokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("code", code)
                        .with("redirect_uri", redirectUri)
                        .with("scope", "read:user,user:email"))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return parseAccessToken(Objects.requireNonNull(responseBody));
    }

    private static String parseAccessToken(String response) {
        String[] parts = response.split("&");
        AtomicReference<String> token = new AtomicReference<>("");
        Arrays.stream(parts).forEach(
                s -> {
                    if (s.startsWith("access_token=")) {
                        token.set(s.substring("access_token=".length()));
                    }
                }
        );

        return token.get();
    }
}
