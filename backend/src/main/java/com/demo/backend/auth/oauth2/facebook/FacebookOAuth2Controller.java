package com.demo.backend.auth.oauth2.facebook;

import com.demo.backend.auth.oauth2.UrlDto;
import com.demo.backend.user.service.UserService;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class FacebookOAuth2Controller {

    @Value("${stripe.security.oauth2.resourceserver.facebook.opaque-token.client-id}")
    private String clientId;
    private String redirectUri = "http://localhost:8081/login";
    @Value("${stripe.security.oauth2.resourceserver.facebook.opaque-token.client-secret}")
    private String clientSecret = "d38ef46998f810e6a2e23319aef1a84a";
    private final UserService userService;

    @GetMapping("/auth/login/facebook/oauth2")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal OAuth2IntrospectionAuthenticatedPrincipal user, HttpServletRequest httpServletRequest) {
        if (user != null) {

            return ResponseEntity.ok(userService.loginViaFacebookOAuth2(user, httpServletRequest.getRemoteAddr()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated via OAuth2");
    }

    @GetMapping("/auth/facebook/url")
    public ResponseEntity<?> authFacebook(HttpServletResponse response) {
        String url = "https://www.facebook.com/v18.0/dialog/oauth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=email,public_profile" +
                "&response_type=code";


        return ResponseEntity.ok(new UrlDto(url));
    }


    @GetMapping("/auth/facebook/callback")
    public ResponseEntity<?> authCallback(@RequestParam("code") String code) {
        FacebookClient facebookClient = new DefaultFacebookClient(Version.LATEST);

        FacebookClient.AccessToken token = facebookClient.obtainUserAccessToken(
                clientId, clientSecret, redirectUri, code);

        return ResponseEntity.ok(token.getAccessToken());
    }
}
