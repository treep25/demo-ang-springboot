package com.demo.backend.auth.controller.auth2fa;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorAuthService {

    private GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public GoogleAuthenticatorKey createSecretKey() {
        return gAuth.createCredentials();
    }

    public boolean verifyCode(UserSecret userSecret, int code) {
        return gAuth.authorize(userSecret.getSecretKey(), code);
    }
}
