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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
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
    private String redirectUri = "http://localhost:8081/azure/calendar";
    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/azure/calendar/auth/url")
    public ResponseEntity<?> auth() throws MalformedURLException {
        String redirectUri = "http://localhost:8081/azure/calendar";
        String encodedScope = URLEncoder.encode("openid profile offline_access Calendars.ReadWrite", StandardCharsets.UTF_8);

        String response = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/authorize?" +
                "client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&response_type=code" +
                "&scope=" + encodedScope;

        return ResponseEntity.ok(new UrlDto(response));
    }

    @GetMapping("/azure/calendar/save/access-token")
    public ResponseEntity<?> setTokenPrincipal(@RequestParam("token") String token, @AuthenticationPrincipal User user) {
        user.setAzureAccessTokenNotRequered(token);

        userService.updateAzureAccessToken(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/azure/calendar/auth/callback")
    public ResponseEntity<?> authCallback(@RequestParam("code") String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("scope", "openid profile offline_access Calendars.ReadWrite");

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
            return ResponseEntity.ok(body.get("access_token"));
        } catch (HttpClientErrorException.BadRequest e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid authorization code");
        }
    }

    @GetMapping("/azure/calendar")
    public ResponseEntity<?> getCalendar(@AuthenticationPrincipal User user) throws Exception {

        return ResponseEntity.ok(azureService.getUserCalendars(user.getAzureAccessTokenNotRequered()));
    }


    @GetMapping("/azure/c/event")
    public ResponseEntity<?> createEvent(@RequestBody EventToCreateAzureModel eventToCreateAzureModel, @AuthenticationPrincipal User user) throws Exception {

        return ResponseEntity.ok(azureService.createEvent(eventToCreateAzureModel));
    }

}
