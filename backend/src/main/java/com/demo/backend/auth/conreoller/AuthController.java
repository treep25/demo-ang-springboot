package com.demo.backend.auth.conreoller;

import com.demo.backend.auth.AuthRequest;
import com.demo.backend.auth.RegRequest;
import com.demo.backend.auth.conreoller.auth2fa.TwoFactorAuthResponse;
import com.demo.backend.auth.conreoller.auth2fa.TwoFactorAuthService;
import com.demo.backend.auth.conreoller.auth2fa.UserSecret;
import com.demo.backend.user.service.UserService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildfly.common.annotation.NotNull;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TwoFactorAuthService twoFactorAuthService;

    @PostMapping("/v1/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest, @NotNull HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(userService.login(authRequest, httpServletRequest.getRemoteAddr()));
    }

    @PostMapping("/v1/auth/register")
    public ResponseEntity<?> register(@RequestBody RegRequest registerRequest) {
        userService.save(registerRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/v1/auth/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken, @NotNull HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(userService.refreshToken(refreshToken, httpServletRequest.getRemoteAddr()));
    }

    @PostMapping("/v2/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        GoogleAuthenticatorKey secretKeyToAuth = twoFactorAuthService.createSecretKey();

        TwoFactorAuthResponse response = userService.saveSecretKeyForUser(authRequest.getEmail(), authRequest.getPassword(), secretKeyToAuth);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/v2/auth/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody AuthRequest verifyCodeRequest, @NotNull HttpServletRequest httpServletRequest) {

        UserSecret userSecret = userService.getUserSecretByUsername(verifyCodeRequest.getEmail(), verifyCodeRequest.getPassword());

        if (userSecret != null) {

            if (twoFactorAuthService.verifyCode(userSecret, verifyCodeRequest.getCode())) {
                return ResponseEntity.ok(userService.login(verifyCodeRequest, httpServletRequest.getRemoteAddr()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authentication code");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
    }
}
