package com.demo.backend.user.service;

import com.demo.backend.auth.AuthRequest;
import com.demo.backend.auth.AuthResponse;
import com.demo.backend.auth.RegRequest;
import com.demo.backend.auth.controller.auth2fa.TwoFactorAuthResponse;
import com.demo.backend.auth.controller.auth2fa.UserSecret;
import com.demo.backend.auth.controller.auth2fa.UserSecretRepository;
import com.demo.backend.config.email.HtmlPageContentBuilder;
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
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserSecretRepository userSecretRepository;
    private final MessageService messageService;
    private static final String APP_NAME = "SPRING ANGULAR";
    private final JavaMailSender javaMailSender;

    private final Map<Predicate<SearchingRequest>, BiFunction<SearchingRequest, UserRepository, List<User>>> searchingMap = Map.of(
            searchingParam -> checkParamssBeforeSearch(searchingParam.getFirstName()) && checkParamssBeforeSearch(searchingParam.getLastName()),
            (currentSearchingParam, repository) -> repository.findByFirstNameAndLastName(currentSearchingParam.getFirstName(), currentSearchingParam.getLastName()),

            searchingParam -> checkParamssBeforeSearch(searchingParam.getLastName()),
            (currentSearchingParam, repository) -> repository.findByLastName(currentSearchingParam.getLastName()),

            searchingParam -> checkParamssBeforeSearch(searchingParam.getFirstName()),
            (currentSearchingParam, repository) -> repository.findByFirstName(currentSearchingParam.getFirstName())
    );

    public UserService(UserRepository userRepository, OrderRepository orderRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtService jwtService, UserSecretRepository userSecretRepository, MessageService messageService, @Qualifier("javaMailSenderSmtp") JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userSecretRepository = userSecretRepository;
        this.messageService = messageService;
        this.javaMailSender = javaMailSender;
    }

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

    public AuthResponse loginViaGithubOAuth2(OAuth2IntrospectionAuthenticatedPrincipal user, String ipAddr) {
        List<String> list = Arrays.stream(user.getClaim("name").toString().split(" ")).toList();

        User findByGithub = userRepository.findByEmail(user.getClaim("email"))
                .orElse(
                        User
                                .builder()
                                .email(user.getClaim("email"))
                                .firstName(list.get(0))
                                .lastName(list.get(1))
                                .isEnabled(true)
                                .role(Role.USER)
                                .provider(Provider.GITHUB)
                                .build()
                );
        User save = userRepository.save(findByGithub);

        return generateResponseTokens(jwtService.generateToken(save, ipAddr), jwtService.generateRefreshToken(save, ipAddr));
    }

    public User save(RegRequest request) {
        User buildUserForSave = User.builder()
                .firstName(request.getFirstName())
                .isEnabled(true)
                .provider(Provider.LOCAL)
                .password(passwordEncoder.encode(request.getPassword()))
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role(Role.USER)
                .build();

        return userRepository.save(buildUserForSave);
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

    public void updateGoogleAccessToken(User user) {
        userRepository.save(user);
    }

    public void updateAzureAccessToken(User user) {
        userRepository.save(user);
    }


    public TwoFactorAuthResponse saveSecretKeyForUser(String email, String password, GoogleAuthenticatorKey secretKey) {
        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        managerAuthentication(AuthRequest.builder().email(email).password(password).build());

        UserSecret userSecret = new UserSecret();
        userSecret.setUser(user);
        userSecret.setSecretKey(secretKey.getKey());

        userSecretRepository.save(userSecret);

        return TwoFactorAuthResponse
                .builder()
                .secretKey(secretKey.getKey())
                .authenticatorUrl(getAuthenticatorUrl(email, secretKey.getKey()))
                .build();
    }

    private String getAuthenticatorUrl(String email, String secretKey) {
        String encodedIssuer = URLEncoder.encode(APP_NAME, StandardCharsets.UTF_8);
        String encodedUsername = URLEncoder.encode(email, StandardCharsets.UTF_8);

        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                encodedIssuer, encodedUsername, secretKey, encodedIssuer);
    }

    public UserSecret getUserSecretByUsername(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        managerAuthentication(AuthRequest.builder().email(email).password(password).build());

        return userSecretRepository.findByUser(user).orElse(null);
    }

    public byte[] generatePdfReport(User user) throws DocumentException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            String userPassword = "1";
            String ownerPassword = "1";
            writer.setEncryption(userPassword.getBytes(), ownerPassword.getBytes(),
                    PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLUE);
            Paragraph title = new Paragraph("User Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            addHtmlContent(document, HtmlPageContentBuilder.generateHtmlContent(user), writer);

            document.close();

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF report with user " + user.getEmail(), e);
        }
    }

    private static void addHtmlContent(Document document, String htmlContent, PdfWriter writer) throws DocumentException {
        try {
            InputStream is = new ByteArrayInputStream(htmlContent.getBytes());
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
        } catch (IOException e) {
            throw new DocumentException("Error adding HTML content to PDF", e);
        }
    }

    public void sendReportViaGmail(User user) throws DocumentException, MessagingException {
        byte[] bytes = generatePdfReport(user);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(user.getEmail());
        helper.setSubject("User Report");
        helper.setText("Please find the attached user report.");

        ByteArrayResource pdfAttachment = new ByteArrayResource(bytes);
        helper.addAttachment("user_report.pdf", pdfAttachment);

        javaMailSender.send(message);
    }
}
