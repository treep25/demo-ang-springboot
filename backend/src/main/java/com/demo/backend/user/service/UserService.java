package com.demo.backend.user.service;

import com.demo.backend.auth.AuthRequest;
import com.demo.backend.auth.AuthResponse;
import com.demo.backend.auth.RegRequest;
import com.demo.backend.order.model.Order;
import com.demo.backend.order.model.OrderStatus;
import com.demo.backend.order.repository.OrderRepository;
import com.demo.backend.security.jwt.JwtService;
import com.demo.backend.support.messages.MessageService;
import com.demo.backend.support.messages.MessageStatus;
import com.demo.backend.user.Provider;
import com.demo.backend.user.Role;
import com.demo.backend.user.SearchingRequest;
import com.demo.backend.user.model.User;
import com.demo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MessageService messageService;

    private final Map<Predicate<SearchingRequest>, BiFunction<SearchingRequest, UserRepository, List<User>>> searchingMap = Map.of(
            searchingParam -> checkParamssBeforeSearch(searchingParam.getFirstName()) && checkParamssBeforeSearch(searchingParam.getLastName()),
            (currentSearchingParam, repository) -> repository.findByFirstNameAndLastName(currentSearchingParam.getFirstName(), currentSearchingParam.getLastName()),

            searchingParam -> checkParamssBeforeSearch(searchingParam.getLastName()),
            (currentSearchingParam, repository) -> repository.findByLastName(currentSearchingParam.getLastName()),

            searchingParam -> checkParamssBeforeSearch(searchingParam.getFirstName()),
            (currentSearchingParam, repository) -> repository.findByFirstName(currentSearchingParam.getFirstName())
    );

    private boolean checkParamssBeforeSearch(String param) {
        return param != null && !param.isEmpty() && !param.isBlank();
    }

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

    public AuthResponse login(AuthRequest authRequest, String ipAddr) {
        User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow(RuntimeException::new);

        managerAuthentication(authRequest);

        return generateResponseTokens(jwtService.generateToken(user, ipAddr), jwtService.generateRefreshToken(user, ipAddr));
    }

    public AuthResponse loginViaGoogleOAuth2(OAuth2IntrospectionAuthenticatedPrincipal user, String ipAddr) {
        User findByGoogle = userRepository.findByEmail(user.getClaim("email"))
                .orElse(
                        User
                                .builder()
                                .email(user.getClaim("email"))
                                .firstName(user.getClaim("given_name"))
                                .lastName(user.getClaim("family_name"))
                                .isEnabled(true)
                                .role(Role.USER)
                                .provider(Provider.GOOGLE)
                                .build()
                );
        User save = userRepository.save(findByGoogle);

        return generateResponseTokens(jwtService.generateToken(save, ipAddr), jwtService.generateRefreshToken(save, ipAddr));
    }

    public AuthResponse loginViaFacebookOAuth2(OAuth2IntrospectionAuthenticatedPrincipal user, String ipAddr) {
        User findByFacebook = userRepository.findByEmail(user.getClaim("email"))
                .orElse(
                        User
                                .builder()
                                .email(user.getClaim("email"))
                                .firstName(user.getClaim("given_name"))
                                .lastName(user.getClaim("family_name"))
                                .isEnabled(true)
                                .role(Role.USER)
                                .provider(Provider.FACEBOOK)
                                .build()
                );
        User save = userRepository.save(findByFacebook);

        return generateResponseTokens(jwtService.generateToken(save, ipAddr), jwtService.generateRefreshToken(save, ipAddr));
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

    public AuthResponse refreshToken(String refreshToken, String ipAddr) {
        String email = jwtService.extractUsername(refreshToken, ipAddr);

        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);

        if (jwtService.isTokenValid(refreshToken, user, ipAddr)) {
            return generateResponseTokens(jwtService.generateToken(user, ipAddr), jwtService.generateRefreshToken(user, ipAddr));
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

    public List<User> searchByFirstNameOrLastName(SearchingRequest inputParams) {

        AtomicReference<List<User>> usersBySearchingRequest = new AtomicReference<>(new ArrayList<>());
        searchingMap.forEach(
                (searchingRequestPredicate, searchingRequestUserRepositoryListBiFunction) -> {
                    if (searchingRequestPredicate.test(inputParams)) {
                        usersBySearchingRequest.set(searchingRequestUserRepositoryListBiFunction.apply(inputParams, userRepository));
                    }
                }
        );
        return usersBySearchingRequest.get();
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

    public Order payOrder(User user) {
        Order order = user.getOrder();

        order.setOrderStatus(OrderStatus.SUCCESSFULLY);
        user.setOrder(order);

        orderRepository.save(order);
        userRepository.save(user);

        return order;
    }

    public Order cancelOrder(User user) {
        Order order = user.getOrder();

        order.setOrderStatus(OrderStatus.CANCELED);
        user.setOrder(order);

        orderRepository.save(order);
        userRepository.save(user);

        return order;
    }
}
