package com.demo.backend.auth.oauth2.azure_calendar;

import com.demo.backend.auth.oauth2.UrlDto;
import com.demo.backend.user.model.User;
import com.demo.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AzureCalendarController {

    private final AzureCalendarService azureService;
    private final UserService userService;
    @Value("${stripe.security.oauth2.resourceserver.azure.activedirectory.client-id}")
    private String clientId;
    @Value("${stripe.security.oauth2.resourceserver.azure.activedirectory.client-secret}")
    private String clientSecret;
    @Value("${stripe.security.oauth2.resourceserver.azure.activedirectory.authority-host}")
    private String authorityHost;
    @Value("${stripe.security.oauth2.resourceserver.azure.activedirectory.tenant-id}")
    private String tenantId;
    private static final String REDIRECT_URI = "http://localhost:8081/azure/calendar";
    private RestTemplate restTemplate = new RestTemplate();
    private static final String SCOPE = "openid profile offline_access Calendars.ReadWrite";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String AUTHORIZATION_CODE = "authorization_code";


    @GetMapping("/azure/calendar/auth/url")
    public ResponseEntity<?> auth() {
        String encodedScope = URLEncoder.encode(SCOPE, StandardCharsets.UTF_8);

        String response = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/authorize?" +
                "client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8) +
                "&response_type=code" +
                "&scope=" + encodedScope;

        return ResponseEntity.ok(new UrlDto(response));
    }

    @GetMapping("/azure/calendar/save/access-token")
    public ResponseEntity<?> setTokenPrincipal
            (
                    @RequestParam("token") String token,
                    @AuthenticationPrincipal User user) {

        user.setAzureAccessTokenNotRequered(token);

        userService.updateAzureAccessToken(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/azure/calendar/auth/callback")
    public ResponseEntity<?> authCallback
            (
                    @RequestParam("code") String code) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", AUTHORIZATION_CODE);
        formData.add("code", code);
        formData.add("redirect_uri", REDIRECT_URI);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("scope", SCOPE);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        try {

            ResponseEntity<Map<String, String>> response = restTemplate
                    .exchange("https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token",
                            HttpMethod.POST,
                            request,
                            new ParameterizedTypeReference<>() {
                            });

            Map<String, String> body = response.getBody();
            return ResponseEntity.ok(body.get(ACCESS_TOKEN));
        } catch (HttpClientErrorException.BadRequest e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid authorization code");
        }
    }

    @GetMapping("/azure/calendar")
    public ResponseEntity<?> getCalendar
            (
                    @AuthenticationPrincipal User user) throws Exception {

        return ResponseEntity.ok(azureService.getUserCalendars(user.getAzureAccessTokenNotRequered()));
    }

    @PostMapping("/azure/c/event")
    public ResponseEntity<?> createEvent
            (
                    @RequestBody EventToCreateAzureModel eventToCreateAzureModel,
                    @AuthenticationPrincipal User user) throws Exception {

        eventToCreateAzureModel.setAccessToken(user.getAzureAccessTokenNotRequered());
        return ResponseEntity.ok(azureService.createEvent(eventToCreateAzureModel));
    }

    @GetMapping("azure/calendar/byDay")
    public ResponseEntity<?> getCalendarByDay
            (
                    @RequestParam("day") String day,
                    @AuthenticationPrincipal User user) throws IOException, URISyntaxException {
        return ResponseEntity.ok(azureService.getEventsByDayOfWeek(user.getAzureAccessTokenNotRequered(), day));
    }

}
