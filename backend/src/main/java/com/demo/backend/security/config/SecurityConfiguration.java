package com.demo.backend.security.config;


import com.demo.backend.config.GoogleOpaqueTokenIntrospector;
import com.demo.backend.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationEntryPoint userAuthEntryPoint;
    private final WebClient userInfoClient;

    @Bean
    public WebMvcConfigurer cors() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8081")
                        .allowedHeaders("*")
                        .allowedMethods("*")
                        .allowCredentials(true);
            }
        };
    }

    public OpaqueTokenIntrospector opaqueTokenIntrospector() {
        return new GoogleOpaqueTokenIntrospector(userInfoClient);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(customizer -> customizer.authenticationEntryPoint(userAuthEntryPoint))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests
                        (authorize ->
                                authorize
                                        .requestMatchers(
                                                "/swagger-ui.html",
                                                "/v2/api-docs",
                                                "/webjars/**",
                                                "/swagger-resources/**",
                                                "/api-docs/**",
                                                "/v3/api-docs/**",
                                                "/configuration/ui",
                                                "/swagger-resources/**",
                                                "/configuration/security",
                                                "/swagger-ui.html",
                                                "/webjars/**")
                                        .permitAll()
                                        .anyRequest()
                                        .permitAll())
                .oauth2ResourceServer(c -> c.opaqueToken(Customizer.withDefaults()))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
