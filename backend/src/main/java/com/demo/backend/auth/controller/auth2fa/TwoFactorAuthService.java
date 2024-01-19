package com.demo.backend.auth.controller.auth2fa;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorAuthService {

    private GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    public GoogleAuthenticatorKey createSecretKey() {
        return googleAuthenticator.createCredentials();
    }

    public boolean verifyCode(UserSecret userSecret, int code) {
        return googleAuthenticator.authorize(userSecret.getSecretKey(), code);
    }
}
