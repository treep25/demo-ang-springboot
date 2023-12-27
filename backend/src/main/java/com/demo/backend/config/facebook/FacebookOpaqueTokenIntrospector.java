package com.demo.backend.config.facebook;


import com.demo.backend.auth.oauth2.facebook.UserInfoFacebook;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FacebookOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final WebClient facebookWebClient;
    public static final String USER_INFO_PATH = "/me";

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        UserInfoFacebook userInfoFacebook = facebookWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(USER_INFO_PATH)
                        .queryParam("fields", "email,first_name,last_name")
                        .queryParam("access_token", token)
                        .build())
                .retrieve()
                .bodyToMono(UserInfoFacebook.class)
                .block();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", userInfoFacebook.getEmail());
        attributes.put("given_name", userInfoFacebook.getFirst_name());
        attributes.put("family_name", userInfoFacebook.getLast_name());

        return new OAuth2IntrospectionAuthenticatedPrincipal(userInfoFacebook.getFirst_name(), attributes, null);
    }

}