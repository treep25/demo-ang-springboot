package com.demo.backend.auth.oauth2.github;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class UserEmailsInfo {
    private String email;
    private boolean primary;

    public boolean getPrimary() {
        return this.primary;
    }
}
