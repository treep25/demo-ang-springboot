package com.demo.backend.auth.oauth2.google_calendar;

import com.demo.backend.auth.oauth2.UrlDto;
import com.demo.backend.user.model.User;
import com.demo.backend.user.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoogleCalendarController {

    @Value("${stripe.security.oauth2.resourceserver.google.opaque-token.client-id}")
    private String clientId;
    @Value("${stripe.security.oauth2.resourceserver.google.opaque-token.client-secret}")
    private String clientSecret;
    private String redirectUri = "http://localhost:8081/calendar";
    private final GoogleCalendarService googleCalendarService;
    private final UserService userService;

    @GetMapping("/google/calendar/auth/url")
    public ResponseEntity<?> auth() throws MalformedURLException {
        String url = new GoogleAuthorizationCodeRequestUrl(
                clientId,
                redirectUri,
                List.of("https://www.googleapis.com/auth/calendar.readonly", "https://www.googleapis.com/auth/calendar.events"))
                .build();

        return ResponseEntity.ok(new UrlDto(url));
    }

    @GetMapping("/google/calendar/auth/callback")
    public ResponseEntity<?> authCallback(@RequestParam("code") String code) throws IOException {
        GoogleTokenResponse execute = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                new GsonFactory(),
                clientId,
                clientSecret,
                code,
                redirectUri

        ).execute();
        String accessToken = execute.getAccessToken();

        return ResponseEntity.ok(accessToken);
    }

    @GetMapping("/google/calendar/save/access-token")
    public ResponseEntity<?> setTokenPrincipal(@RequestParam("token") String token, @AuthenticationPrincipal User user) {
        user.setGoogleAccessTokenNotRequered(token);

        userService.updateGoogleAccessToken(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/google/calendar")
    public ResponseEntity<?> getEventsForTheWeek(@AuthenticationPrincipal User user) throws GeneralSecurityException, IOException {
        List<Event> eventsForTheWeek = googleCalendarService.getEventsForTheWeek(user.getGoogleAccessTokenNotRequered());
        if (eventsForTheWeek != null) {
            return ResponseEntity.ok(eventsForTheWeek);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("google/calendar/c/event")
    public ResponseEntity<?> createEvent(@RequestBody EventToCreateModel eventToCreateModel, @AuthenticationPrincipal User user) throws GeneralSecurityException, IOException {

        eventToCreateModel.setToken(user.getGoogleAccessTokenNotRequered());

        return new ResponseEntity<>(googleCalendarService.createEvent(eventToCreateModel), HttpStatusCode.valueOf(201));
    }

    @GetMapping("google/calendar/byDay")
    public ResponseEntity<?> getCalendarByDay(@RequestParam("day") String day, @AuthenticationPrincipal User user) throws GeneralSecurityException, IOException {
        return ResponseEntity.ok(googleCalendarService.getCalendarByDay(day, user.getGoogleAccessTokenNotRequered()));
    }
}
