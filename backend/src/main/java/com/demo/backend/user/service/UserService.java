package com.demo.backend.user.service;

import com.demo.backend.auth.AuthRequest;
import com.demo.backend.auth.AuthResponse;
import com.demo.backend.auth.RegRequest;
import com.demo.backend.order.Order;
import com.demo.backend.order.OrderRepository;
import com.demo.backend.security.jwt.JwtService;
import com.demo.backend.support.messages.MessageService;
import com.demo.backend.support.messages.MessageStatus;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MessageService messageService;

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

    public List<User> getAllUserExcludeCurrent(long userId) {
        return userRepository.findAll()
                .stream()
                .filter(user -> userId != user.getId())
                .toList();
    }

    public void changeStatusOfUser(long id) {
        User user = userRepository.findById(id).orElseThrow(RuntimeException::new);
        user.changeEnableAbility();

        userRepository.save(user);
    }

    public List<User> searchByFirstName(String firstName) {
        return userRepository.findByFirstName(firstName);
    }

    public List<User> searchByLastName(String lastName) {
        return userRepository.findByLastName(lastName);
    }

    public List<User> getAllEnabledUsers(long currentUserId) {
        return userRepository.findAllByIsEmableTrue().stream().filter(user -> user.getId() != currentUserId).toList();
    }

    public void clearOrders(User user) {
        Order order = user.getOrder();
        user.setOrder(null);
        userRepository.save(user);
        orderRepository.delete(order);
    }

    public long getAllUnreadMessagesWith(User currentUser, String email) {
        return messageService.getAllUnreadMessagesWith(currentUser, email);
    }

    public long getAllUnreadMessagesOf(User currentUser) {
        return currentUser.getReceivedMessages()
                .stream()
                .filter(message -> message.getMessageStatus().equals(MessageStatus.UNREAD))
                .count();
    }
}
