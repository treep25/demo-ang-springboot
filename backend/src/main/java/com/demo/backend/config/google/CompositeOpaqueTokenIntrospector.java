package com.demo.backend.config.google;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
@Slf4j
public class CompositeOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
    private final Map<String, OpaqueTokenIntrospector> introspectors;
    private final Map<Predicate<String>, Function<String, OAuth2AuthenticatedPrincipal>> introspectorsFunction;

    public CompositeOpaqueTokenIntrospector(Map<String, OpaqueTokenIntrospector> introspectors) {
        this.introspectors = introspectors;
        this.introspectorsFunction = Map.of
                (
                        token -> token.startsWith("GOOGLE"), token -> introspectors.get("GOOGLE").introspect(token.replaceAll("GOOGLE", "")),
                        token -> token.startsWith("FACEBOOK"), token -> introspectors.get("FACEBOOK").introspect(token.replaceAll("FACEBOOK", ""))

                );
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        AtomicReference<OAuth2AuthenticatedPrincipal> apply = new AtomicReference<>();
        introspectorsFunction
                .forEach((tokenPredicate, introspectorsFunction) -> {
                    if (tokenPredicate.test(token)) {
                        apply.set(introspectorsFunction.apply(token));
                    }
                });
        return apply.get();
    }
}
