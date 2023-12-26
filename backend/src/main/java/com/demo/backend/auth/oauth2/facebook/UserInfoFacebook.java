package com.demo.backend.auth.oauth2.facebook;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class UserInfoFacebook {
    private String id;
    private String first_name;
    private String last_name;
    private String email;
}
