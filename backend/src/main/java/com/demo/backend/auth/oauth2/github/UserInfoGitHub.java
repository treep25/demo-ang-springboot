package com.demo.backend.auth.oauth2.github;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class UserInfoGitHub {
    private String id;
    private String email;
    private String name;
}
