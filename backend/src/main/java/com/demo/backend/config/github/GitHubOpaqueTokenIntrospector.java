package com.demo.backend.config.github;


import com.demo.backend.auth.oauth2.github.UserEmailsInfo;
import com.demo.backend.auth.oauth2.github.UserInfoGitHub;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GitHubOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final WebClient githubWebClient;
    public static final String USER_INFO_PATH = "/user";
    public static final String USER_EMAILS_INFO_PATH = USER_INFO_PATH + "/emails";

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        UserInfoGitHub userInfoGitHub = githubWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(USER_INFO_PATH)
                        .build())
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .bodyToMono(UserInfoGitHub.class)
                .block();

        List<UserEmailsInfo> userEmailsInfos = githubWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(USER_EMAILS_INFO_PATH)
                        .build())
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserEmailsInfo>>() {
                })
                .block();

        String email = Objects.requireNonNull(userEmailsInfos).stream().filter(UserEmailsInfo::getPrimary).toList().get(0).getEmail();

        Objects.requireNonNull(userInfoGitHub).setEmail(email);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", userInfoGitHub.getEmail());
        attributes.put("name", userInfoGitHub.getName());

        return new OAuth2IntrospectionAuthenticatedPrincipal(userInfoGitHub.getName(), attributes, null);
    }

}