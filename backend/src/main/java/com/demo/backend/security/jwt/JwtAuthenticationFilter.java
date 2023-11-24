package com.demo.backend.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.InputStream;
import java.io.OutputStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final Object credentials = null;

    private void authenticateToken(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                credentials,
                userDetails.getAuthorities()
        );
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {
        final Cookie[] cookies = request.getCookies();
        String jwt = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        System.out.println(jwt);
        final String username;
        try {
            if (jwt == null) {
                filterChain.doFilter(request, response);
                return;
            }
            username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    authenticateToken(userDetails, request);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            response.setStatus(response.getStatus());
            response.setContentType("text/html");
            request.setAttribute("errorMessage", ex.getMessage());
            try (InputStream is = getClass().getResourceAsStream("/templates/jwtError.html");
                 OutputStream os = response.getOutputStream()) {
                IOUtils.copy(is, os);
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

