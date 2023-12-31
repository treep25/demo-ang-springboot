package com.demo.backend.config.google;

import com.demo.backend.auth.oauth2.google.UserInfoGoogle;
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

    private final WebClient googleWebClient;
    public static final String USER_INFO_PATH = "/oauth2/v3/userinfo";

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        UserInfoGoogle userInfoGoogle = googleWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(USER_INFO_PATH)
                        .queryParam("access_token", token)
                        .build())
                .retrieve()
                .bodyToMono(UserInfoGoogle.class)
                .block();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", Objects.requireNonNull(userInfoGoogle).getSub());
        attributes.put("email", userInfoGoogle.getEmail());
        attributes.put("given_name", userInfoGoogle.getGiven_name());
        attributes.put("family_name", userInfoGoogle.getFamily_name());
        attributes.put("picture", userInfoGoogle.getPicture());
        return new OAuth2IntrospectionAuthenticatedPrincipal(userInfoGoogle.getName(), attributes, null);
    }
}
