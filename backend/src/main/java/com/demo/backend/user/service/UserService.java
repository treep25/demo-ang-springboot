package com.demo.backend.user.service;

import com.demo.backend.auth.AuthRequest;
import com.demo.backend.auth.AuthResponse;
import com.demo.backend.auth.RegRequest;
import com.demo.backend.security.jwt.JwtService;
import com.demo.backend.user.Role;
import com.demo.backend.user.model.User;
import com.demo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private void managerAuthentication(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new AccessDeniedException("incorrect login or password");
        }
    }

    public AuthResponse login(AuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow(RuntimeException::new);

        managerAuthentication(authRequest);

        return generateResponseTokens(jwtService.generateToken(user), jwtService.generateRefreshToken(user));
    }

    public void save(RegRequest request) {
        User buildUserForSave = User.builder()
                .firstName(request.getFirstName())
                .password(passwordEncoder.encode(request.getPassword()))
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role(Role.USER)
                .build();

        userRepository.save(buildUserForSave);
    }

    private AuthResponse generateResponseTokens(String accessToken, String refreshToken) {

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);

        if (jwtService.isTokenValid(refreshToken, user)) {
            return generateResponseTokens(jwtService.generateToken(user), jwtService.generateRefreshToken(user));
        }
        throw new RuntimeException("This token is not valid -> could`nt refresh");
    }

}
