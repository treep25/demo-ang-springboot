package com.demo.backend.config.google;

import com.demo.backend.auth.oauth2.google.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GoogleOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final WebClient userInfoClient;
    public static final String USER_INFO_PATH = "/oauth2/v3/userinfo";

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        UserInfo userInfo = userInfoClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(USER_INFO_PATH)
                        .queryParam("access_token", token)
                        .build())
                .retrieve()
                .bodyToMono(UserInfo.class)
                .block();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", Objects.requireNonNull(userInfo).getSub());
        attributes.put("email", userInfo.getEmail());
        attributes.put("given_name", userInfo.getGiven_name());
        attributes.put("family_name", userInfo.getFamily_name());
        attributes.put("picture", userInfo.getPicture());
        return new OAuth2IntrospectionAuthenticatedPrincipal(userInfo.getName(), attributes, null);
    }
}
